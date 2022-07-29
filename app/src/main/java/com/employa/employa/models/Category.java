package com.employa.employa.models;

import androidx.annotation.NonNull;

import com.employa.employa.R;

public enum Category {
    BABYSIT("Babysit", R.drawable.ic_babysit_24dp),
    TUIN("Tuin", R.drawable.ic_tuin_24dp),
    HERSTELLINGEN("Herstellingen", R.drawable.ic_herstellingen_24dp),
    INFORMATICA("Informatica", R.drawable.ic_informatica_24dp),
    POETSHULP("Poetshulp", R.drawable.ic_poetshulp_24dp),
    BIJLES("Bijles", R.drawable.ic_bijles_24dp),
    OVERIGE("Overige", R.drawable.ic_overige_24dp);

    private String name;
    private int icon;

    Category(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    @Override
    @NonNull
    public String toString() {
        return getName();
    }

    public String getName() {
        return this.name;
    }

    public int getIcon() {
        return icon;
    }
}
