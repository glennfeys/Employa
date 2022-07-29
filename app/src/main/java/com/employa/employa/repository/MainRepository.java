package com.employa.employa.repository;


import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.algolia.search.saas.AbstractQuery;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.employa.employa.R;
import com.employa.employa.models.Advertisement;
import com.employa.employa.models.Radius;
import com.employa.employa.models.Rating;
import com.employa.employa.models.User;
import com.employa.employa.utility.Consumer;
import com.employa.employa.viewmodel.SearchViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainRepository {
    public static final String UID_BUNDLE = "uid";
    public static final String BUNDLE_ADVERTISEMENT_ID = "advertisementID";

    private static final String TAG = "Main Repository";
    // Algolia API details
    private static final String ALGOLIA_APP_ID = "8K67NQ88ZR";
    private static final String ALGOLIA_SEARCH_API_KEY = "5e7e4b8f199a2a1634002ea995489e3c";
    private static final String ALGOLIA_INDEX_NAME = "advertisements";
    private static MainRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseStorage firebaseStorage;
    private Index index;

    private MainRepository() {
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        Client client = new Client(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY);
        index = client.getIndex(ALGOLIA_INDEX_NAME);
    }

    /**
     * Gets an instance of the main repository.
     *
     * @return Instance of main repository
     */
    public static synchronized MainRepository getInstance() {
        if (instance == null)
            instance = new MainRepository();

        return instance;
    }

    /**
     * Is it me?
     *
     * @param uid User ID
     * @return True if me, false otherwise
     */
    public boolean isMe(String uid) {
        return FirebaseAuth.getInstance().getUid().equals(uid);
    }

    /**
     * Get current user document reference
     *
     * @return Current user document reference
     */
    private DocumentReference getCurrentUserDocRef() {
        String uid = FirebaseAuth.getInstance().getUid();
        assert uid != null;
        return db.collection("users").document(uid);
    }

    /**
     * Adds an advertisement.
     *
     * @param data The data
     * @param cb   The callback
     */
    public void addAdvertisement(Map<String, Object> data, Callback<Task> cb) {
        data.put("ownerRef", getCurrentUserDocRef());
        db.collection("advertisements").add(data).addOnCompleteListener(cb::onSuccess).addOnFailureListener(e -> cb.onFail());
    }

    /**
     * Deletes an advertisement.
     *
     * @param advertisement The advertisment you want to delete
     * @param cb            The callback
     */
    public void deleteAdvertisement(Advertisement advertisement, Callback<Task> cb) {
        db.collection("advertisements").document(advertisement.getId())
                .delete().addOnCompleteListener(cb::onSuccess).addOnFailureListener(e -> cb.onFail());
    }

    /**
     * Set advertisement data in live data
     *
     * @param advertisementID Ad id
     * @param advertisement   Ad live data object
     * @param user            User live data
     */
    public void setAdvertisement(String advertisementID, MutableLiveData<Advertisement> advertisement, MutableLiveData<User> user, MutableLiveData<Double> avgScore, MutableLiveData<Integer> avgScoreSize) {
        db.collection("advertisements").document(advertisementID)
                .get().addOnSuccessListener(documentSnapshot -> {
            Advertisement myAdvertisement = documentSnapshot.toObject(Advertisement.class);
            advertisement.setValue(myAdvertisement);
            fillUser(myAdvertisement.getOwnerRef(), user);
            setAvgScore(myAdvertisement.getOwnerRef(), avgScore, avgScoreSize);
        });
    }

    /**
     * Build query from data
     *
     * @param search The search model
     * @return The query
     */
    Query buildQuery(SearchViewModel search) {
        String q = (search.getQuery().getValue() == null) ? "" : search.getQuery().getValue();

        // Om alleen relevante zoekertjes te tonen
        // Kijken we naar alle zoekertjes met een datums
        // vanaf vandaag.
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);

        // Basisquery: zoekertjes op vandaag of later of zonder tijdstippen
        // + toon geen eigen zoekertjes in de feed
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        Query query = new Query(q).setFilters("timestamps >= " + now.getTime().getTime() / 1000L + " OR no_timestamps=1 AND NOT owner:" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Als er categorieën zijn geselecteerd, voeg deze toe aan de query
        List<String> categories = search.getCategories().getValue();
        query = setFacetListFilter("category", categories, query);

        // Stel datumfiltering in
        List<Calendar> dateRange = search.getPeriod().getValue();
        query = setDateRangeFilter(dateRange, query);

        // Filter op afstand.
        Radius radiusValue = search.getRadius().getValue();
        query = setRadiusFilter(radiusValue, query);

        return query;
    }

    /**
     * Set distance filtering for this Query
     *
     * @param radius Radius to filter by
     * @param query  Current query
     */
    private Query setRadiusFilter(Radius radius, Query query) {
        if (radius != null) {
            Location center = radius.getCenter();
            return query.setAroundLatLng(new AbstractQuery.LatLng(center.getLatitude(), center.getLongitude())).setAroundRadius(radius.getSize() * 1000);
        }
        return query;
    }

    /**
     * From a list of facetValues and the name of the facet build
     * an Algolia compatible Query
     *
     * @param facetName   Name of the Algolia facet
     * @param facetValues List of values for this facet
     * @return Algolia query
     */
    private Query setFacetListFilter(String facetName, List<String> facetValues, Query query) {
        String filter = query.getFilters();
        if (facetValues != null && facetValues.size() > 0) {
            List<String> facetFilters = new ArrayList<>();
            for (String facetValue : facetValues) {
                facetFilters.add(facetName + ":" + facetValue);
            }
            filter += " AND (" + TextUtils.join(" OR ", facetFilters) + ")";
        }
        return query.setFilters(filter);
    }

    /**
     * From the list of Calendars build an Algolia Filter
     *
     * @param dateRange Ascendingly sorted list of Calendars
     * @return Algolia query
     */
    private Query setDateRangeFilter(List<Calendar> dateRange, Query query) {
        String filter = query.getFilters();
        if (dateRange != null) {
            long time;
            if (dateRange.size() >= 2)
                time = dateRange.get(dateRange.size() - 1).getTime().getTime() / 1000L;
            else // dateRange.size() == 1
                time = (dateRange.get(0).getTime().getTime() + 1000L) / 1000L;

            filter += " AND (timestamps >= " + dateRange.get(0).getTime().getTime() / 1000L + " AND timestamps <= " + time + ")";
        }
        return query.setFilters(filter);
    }

    /**
     * Get advertisements from Algolia using a Query. (Capped to 1000 results)
     *
     * @param search         Search model
     * @param advertisements Live data from the view model
     * @param callback       A function that consumes the number of advertisements found
     */
    public void getAdvertisementsByQuery(SearchViewModel search, final MutableLiveData<List<Advertisement>> advertisements, Consumer<Integer> callback) {
        // Algolia Logic
        index.searchAsync(buildQuery(search), (jsonObject, e) -> {
            List<Advertisement> results = new ArrayList<>();
            try {
                JSONArray hits = jsonObject.getJSONArray("hits");
                for (int i = 0; i < hits.length(); i++) {
                    JSONObject hit = hits.getJSONObject(i);

                    Advertisement ad = new Advertisement();

                    ad.setCategory(hit.getString("category"));
                    ad.setTitle(hit.getString("title"));
                    ad.setDescription(hit.getString("description"));

                    ad.setCreated(new Timestamp(new Date(hit.getLong("created") * 1000)));

                    JSONObject geoloc = hit.getJSONObject("_geoloc");
                    ad.setLocation(new GeoPoint(geoloc.getDouble("lat"), geoloc.getDouble("lng")));
                    ad.setPayment(hit.getString("payment"));

                    List<Timestamp> timestamps = new ArrayList<>();
                    if (hit.optJSONArray("timestamps") != null) {
                        JSONArray timestampArray = hit.getJSONArray("timestamps");
                        for (int j = 0; j < timestampArray.length(); j++) {
                            timestamps.add(new Timestamp(new Date(timestampArray.getLong(j) * 1000)));
                        }
                    }
                    ad.setTimestamps(timestamps);
                    ad.setOwnerRef(db.collection("users").document(hit.getString("owner")));

                    ad.setId(hit.getString("objectID"));
                    results.add(ad);
                }

                // Advertisements are now in results, next we should enrich them with the Owner

                List<Task<Advertisement>> enrichmentTasks = new ArrayList<>(results.size());
                for (Advertisement ad : results)
                    enrichmentTasks.add(enrichAdvertisement(ad));

                Tasks.<Advertisement>whenAllSuccess(enrichmentTasks).addOnSuccessListener(ads -> {
                    advertisements.postValue(ads);
                    if (callback != null)
                        callback.accept(ads.size());
                });
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private Task<Advertisement> enrichAdvertisement(final Advertisement ad) {
        final DocumentReference ownerReference = ad.getOwnerRef();
        return ownerReference.get().onSuccessTask(data -> {
            User owner = data.toObject(User.class);
            owner.setId(ownerReference.getId());

            return firebaseStorage.getReferenceFromUrl(owner.getProfilePicture()).getDownloadUrl().continueWith(task -> {
                if (task.isSuccessful()) {
                    owner.setProfilePicDownload(task.getResult());
                    ad.setOwner(owner);
                    return ad;
                } else {
                    return null;
                }
            });
        });
    }

    public void getUser(String uid, MutableLiveData<User> userData) {
        userData.setValue(null);
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            user.setId(documentSnapshot.getId());
            firebaseStorage.getReferenceFromUrl(user.getProfilePicture())
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        user.setProfilePicDownload(uri);
                        userData.setValue(user);
                    });
        });
    }

    public void getEditUser(String uid, MutableLiveData<User> userData, MutableLiveData<String> name, MutableLiveData<String> description) {
        userData.setValue(null);
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            user.setId(documentSnapshot.getId());
            firebaseStorage.getReferenceFromUrl(user.getProfilePicture())
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        user.setProfilePicDownload(uri);
                        userData.setValue(user);
                        name.setValue(user.getName());
                        description.setValue(user.getDescription());
                    });
        });
    }

    private void fillUser(DocumentReference userReference, final MutableLiveData<User> user) {
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            final User myUser = documentSnapshot.toObject(User.class);
            myUser.setId(userReference.getId());

            firebaseStorage.getReferenceFromUrl(myUser.getProfilePicture())
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        myUser.setProfilePicDownload(uri);
                        user.setValue(myUser);
                    });
        });
    }

    public void editUser(String uid, String name, String description, String photoRef, Callback<Task> cb) {
        db.collection("users").document(uid).update("name", name, "description", description, "profilePicture", photoRef)
                .addOnCompleteListener(cb::onSuccess).addOnFailureListener(e -> cb.onFail());
    }

    public void createRating(String uid, int score, String description, Callback<Task> cb) {
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        Rating rating = new Rating(userRef, score, description, Timestamp.now());
        db.collection("users").document(uid).collection("ratings").add(rating)
                .addOnCompleteListener(cb::onSuccess).addOnFailureListener(e -> cb.onFail());
    }

    public void editRating(String uid, String ratingID, int score, String description, Callback<Task> cb) {
        db.collection("users").document(uid).collection("ratings").document(ratingID)
                .update("score", score, "description", description, "created", Timestamp.now())
                .addOnCompleteListener(cb::onSuccess).addOnFailureListener(e -> cb.onFail());
    }

    public void getRating(String uid, String ratingID, MutableLiveData<Rating> ratingData, MutableLiveData<String> description, MutableLiveData<Integer> score) {
        db.collection("users").document(uid).collection("ratings").document(ratingID).get().addOnSuccessListener(documentSnapshot -> {
            Rating rating = documentSnapshot.toObject(Rating.class);
            ratingData.setValue(rating);
            description.setValue(rating.getDescription());
            score.setValue(rating.getScore());
        });
    }

    private double getAvgScore(List<Rating> ratings) {
        double sum = 0;

        for (Rating r : ratings)
            sum += r.getScore();

        if (ratings.size() == 0) return sum;
        return sum / ratings.size();
    }

    private void setAvgScore(DocumentReference ownerRef, MutableLiveData<Double> avgScore, MutableLiveData<Integer> size) {
        ownerRef.collection("ratings").get().addOnSuccessListener(qs -> {
            double sum = 0.0;

            for (DocumentSnapshot document : qs.getDocuments())
                sum += Objects.requireNonNull(document.getDouble("score"));

            size.setValue(qs.getDocumentChanges().size());
            avgScore.setValue(qs.getDocumentChanges().size() == 0 ? sum : sum / qs.getDocumentChanges().size());
        });
    }

    public void editPhoto(Uri photoUri, ImageView imageView, MutableLiveData<String> photoData) {
        StorageReference chatPhotoReference = firebaseStorage.getReference().child("profile_pictures");
        StorageReference photoRef = chatPhotoReference.child(UUID.randomUUID().toString());

        //upload file to firebase storage
        photoRef.putFile(photoUri).addOnSuccessListener(taskSnapshot ->
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(imageView.getContext())
                            .load(uri)
                            .into(imageView);
                    photoData.setValue(photoRef.toString());
                }));
    }

    public RegisteredListener getAdvertisementsFromUser(String uid, MutableLiveData<List<Advertisement>> advertisements) {
        List<Advertisement> advertisementList = new ArrayList<>();
        advertisements.setValue(advertisementList);

        DocumentReference ownerRef = db.collection("users").document(uid);
        return snapshotListenerHelper(db.collection("advertisements").whereEqualTo("ownerRef", ownerRef).orderBy("created"), changes -> {
            for (DocumentChange dc : changes) {
                if (dc.getType() == DocumentChange.Type.REMOVED || dc.getType() == DocumentChange.Type.MODIFIED) {
                    String id = dc.getDocument().getId();
                    for (int i = 0; i < advertisementList.size(); i++) {
                        if (advertisementList.get(i).getId().equals(id)) {
                            advertisementList.remove(i);
                            break;
                        }
                    }

                }
                if (dc.getType() != DocumentChange.Type.REMOVED) {
                    Advertisement advertisement = dc.getDocument().toObject(Advertisement.class);
                    advertisement.setId(dc.getDocument().getId());
                    advertisementList.add(advertisement);
                }
            }
            advertisements.setValue(advertisementList);
        });
    }

    public void getAdvertisement(String id, Callback<Advertisement> advertisementData) {
        db.collection("advertisements").document(id).get().addOnSuccessListener(documentSnapshot -> {
            Advertisement advertisement = documentSnapshot.toObject(Advertisement.class);
            advertisement.setId(documentSnapshot.getId());
            advertisementData.onSuccess(advertisement);
        });
    }

    public void editAdvertisement(String id, Map<String, Object> data, Callback<Task> cb) {
        db.collection("advertisements").document(id).update(data)
                .addOnCompleteListener(cb::onSuccess).addOnFailureListener(e -> cb.onFail());
    }

    public RegisteredListener getRatings(String uid, MutableLiveData<List<Rating>> ratingsData, MutableLiveData<Double> avgScore, MutableLiveData<Integer> size) {
        List<Rating> ratings = new ArrayList<>();
        ratingsData.setValue(ratings);
        avgScore.setValue(0.0);

        return snapshotListenerHelper(db.collection("users").document(uid).collection("ratings"), changes -> {
            for (DocumentChange dc : changes) {
                if (dc.getType() == DocumentChange.Type.MODIFIED) {
                    for (Rating r : ratings) {
                        if (r.getId().equals(dc.getDocument().getId())) {
                            ratings.remove(r);
                            break;
                        }
                    }
                }
                Rating newRating = dc.getDocument().toObject(Rating.class);
                newRating.setId(dc.getDocument().getId());
                newRating.getUserReference().get().addOnSuccessListener(documentSnapshot -> {
                    User reviewer = documentSnapshot.toObject(User.class);
                    reviewer.setId(documentSnapshot.getId());
                    StorageReference profilePic = firebaseStorage.getReferenceFromUrl(reviewer.getProfilePicture());
                    profilePic.getDownloadUrl().addOnSuccessListener(uri -> {
                        reviewer.setProfilePicDownload(uri);
                        newRating.setUser(reviewer);
                        ratings.add(newRating);
                        ratingsData.setValue(ratings);
                        avgScore.setValue(getAvgScore(ratings));
                        size.setValue(ratings.size());
                    });
                });
            }
        });
    }

    public void updateEmail(String newEmail, String password, Callback<Task> cb) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updateEmail(newEmail)
                                .addOnSuccessListener(t -> user.sendEmailVerification().addOnCompleteListener(cb::onSuccess)).addOnFailureListener(e -> cb.onFail());
                    } else {
                        cb.onFail();
                    }
                });
    }


    /**
     * Helper for snapshot listener events
     *
     * @param q  Query
     * @param cb Callback
     * @return Listener registration
     */
    private RegisteredListener snapshotListenerHelper(com.google.firebase.firestore.Query q, Consumer<List<DocumentChange>> cb) {
        return new RegisteredListener(q.addSnapshotListener(((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, e.getMessage());
                return;
            }

            if (queryDocumentSnapshots != null)
                cb.accept(queryDocumentSnapshots.getDocumentChanges());
        })));
    }
}
