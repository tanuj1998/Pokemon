package com.tanuj.pokemon;

import android.location.Location;

public class Pokemon {
    public int Image;
    public String name;
    public String des;
    public double power;
    public boolean isCatch;
    public Location location;

    Pokemon(int Image, String name, String des, double power, double lat, double lag ){
        this.Image = Image;
        this.name = name;
        this.des = des;
        this.power = power;
        this.isCatch = false;
        location = new Location(name);
        location.setLongitude(lag);
        location.setLatitude(lat);
    }
}
