package com.employa.employa.models;

import android.location.Location;

/**
 * A Radius is a circle in a 2D-coordinate system with a center (a Location) and a radius.
 */
public class Radius {
    private Location center;
    private Integer radius;

    /**
     * A Radius is a circle in a 2D-coordinate system with a center (a Location) and a radius.
     * @param center Location middle point
     * @param radius Circle radius
     */
    public Radius(Location center, Integer radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * Get the center of our circle radius
     * @return Location middle point
     */
    public Location getCenter() {
        return center;
    }

    /**
     * Get size of radius
     * @return Radius
     */
    public Integer getSize() {
        return radius;
    }
}
