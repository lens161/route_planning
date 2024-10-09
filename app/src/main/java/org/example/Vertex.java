package org.example;

public class Vertex {
    private int ID;
    private float longitude;
    private float latitude;

    public Vertex(int id, float lo, float la){
        this.ID = id;
        this.latitude = la; 
        this.longitude = lo;
    }

    public int getID() {
        return ID;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    
}
