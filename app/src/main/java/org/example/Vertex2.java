package org.example;


public class Vertex2 {
    private long id;
    private int index;
    private float longitude;
    private float latitude;

    public Vertex2(long id, int index, float longitude, float latitude) {
        this.id = id;
        this.index = index;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String toString() {
        return index + " - ID: " + id + " longitude: " + longitude + " latitude: " + latitude;
    }
}
