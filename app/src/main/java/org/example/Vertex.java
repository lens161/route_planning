package org.example;

public class Vertex {
    private int ID;
    private int index;
    private float longitude;
    private float latitude;

    public Vertex(int id, int index, float lo, float la){
        this.ID = id;
        this.latitude = la; 
        this.longitude = lo;
    }

    public int ID() {
        return ID;
    }

    public int index(){
        return index;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String toString(){
        return index + " - ID: " + ID + "longitude: " + longitude + "latitude: " + latitude; 
    }

    
}
