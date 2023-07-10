package com.example.altezachen3;

import java.io.Serializable;

public class ItemSize implements Serializable //מחלקת גדלים
{
    private double itemX;//אורך
    private double itemY;//רוחב
    private double itemZ;//גובה

    public ItemSize() {
    }

    public ItemSize(double itemX, double itemY, double itemZ) {
        this.itemX = itemX;
        this.itemY = itemY;
        this.itemZ = itemZ;
    }

    public double getItemX() {
        return itemX;
    }

    public void setItemX(double itemX) {
        this.itemX = itemX;
    }

    public double getItemY() {
        return itemY;
    }

    public void setItemY(double itemY) {
        this.itemY = itemY;
    }

    public double getItemZ() {
        return itemZ;
    }

    public void setItemZ(double itemZ) {
        this.itemZ = itemZ;
    }
    //טענת יציאה: נפח המוצר
    public double Volume()
    {
        return itemX*itemY*itemZ;
    }
    @Override
    public String toString() {
        return "ItemSizeInMETERS{" +
                "itemX=" + itemX +
                ", itemY=" + itemY +
                ", itemZ=" + itemZ +
                '}';
    }
}

