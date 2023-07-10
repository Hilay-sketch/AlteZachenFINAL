package com.example.altezachen3;

import java.io.Serializable;

public class Item implements Serializable {
    private String idItem;//id לזהוי אחרי זה
    private String itemName;//שם המוצר
    private double itemPrice;//מחיר המוצר
    private int itemQuantity;//כמה יש מהמוצר
    private ItemSize itemSize;//גדלי המוצר
    private String category;//קטוגריה של המוצר(ספות/כורסאות/שולחנות וכו')
    public String itImagePath;//רפרנס לדאטאבייס פאט של האימג
    public String creatorMail;//מייל היוצר של העצם


    public Item() {
    }//פעולה ריקה

    public Item(String itemName, double itemPrice, int itemQuantity, ItemSize itemSize2, String category, String itImagePath, String creatorMail) {
        this.itemName = itemName;
        this.idItem = null;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.itemSize = itemSize2;
        this.category = category;
        this.itImagePath = itImagePath;
        this.creatorMail = creatorMail;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public ItemSize getItemSize() {
        return itemSize;
    }

    public void setItemSize(ItemSize itemSize) {
        this.itemSize = itemSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getItImagePath() {
        return itImagePath;
    }

    public void setItImagePath(String itImagePath) {
        this.itImagePath = itImagePath;
    }

    public String getCreatorMail() {
        return creatorMail;
    }

    public void setCreatorMail(String creatorMail) {
        this.creatorMail = creatorMail;
    }

    @Override
    public String toString() {
        return itemName;
    }
}
