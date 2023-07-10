package com.example.altezachen3;

import java.util.ArrayList;

//אב המחלקות בתוכו התכונות הנמצאות אצל כל הפרופילים האפשריים
public class Profile {
    protected String accountName;//שם המשתמש
    protected String accountEmail;//אימייל חשבון לחשבון
    protected ArrayList<String> wishListItemsID = new ArrayList<String>(); // מערך של המוצרים האהובים של המשתמש
    protected String phoneNumber;//מס' פלאפון של החשבון/העסק
    protected String profilePicturePath;//גישה לתמונת פרופיל של החשבון
    protected boolean isSeller;//בודק אם יצרן

    public Profile() {
    }
    //פעולה בונה
    public Profile(String accountName, String accountEmail,  String phoneNumber, String profilePicturePath) {
        this.accountName = accountName;
        this.accountEmail = accountEmail;
        this.wishListItemsID = new ArrayList<String>();
        this.phoneNumber = phoneNumber;
        this.profilePicturePath = profilePicturePath;
        this.isSeller = false;
    }

    public boolean isSeller() {
        return isSeller;
    }

    public void setSeller(boolean seller) {
        isSeller = seller;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public ArrayList<String> getWishListItemsID() {
        return wishListItemsID;
    }

    public void setWishListItemsID(ArrayList<String> wishListItemsID) {
        this.wishListItemsID = wishListItemsID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "accountName='" + accountName + '\'' +
                ", accountEmail='" + accountEmail + '\'' +
                ", wishListItemsID=" + wishListItemsID +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePicturePath='" + profilePicturePath + '\'' +
                ", isSeller=" + isSeller +
                '}';
    }
}

