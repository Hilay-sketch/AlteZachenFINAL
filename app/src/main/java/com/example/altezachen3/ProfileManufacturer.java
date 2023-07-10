package com.example.altezachen3;

import java.util.ArrayList;

public class ProfileManufacturer extends Profile
{
    private String location;//מיקום החברה
    private ArrayList<String> postsList;//רשימה של כל המוצרים שיןצר
    public ProfileManufacturer(){}

    //פעולה בונה
    public ProfileManufacturer(String accountName, String accountEmail, String phoneNumber, String profilePicturePath, String location) {
        super(accountName, accountEmail,  phoneNumber, profilePicturePath);
        this.location = location;
        this.postsList = new ArrayList<String>();
        super.isSeller = true;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getPostsList() {
        return postsList;
    }

    public void setPostsList(ArrayList<String> postsList) {
        this.postsList = postsList;
    }

    @Override
    public String toString() {
        return "ProfileManufacturer{" +
                "location='" + location + '\'' +
                ", postsList=" + postsList +
                ", accountName='" + accountName + '\'' +
                ", accountEmail='" + accountEmail + '\'' +
                ", wishListItemsID=" + wishListItemsID +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePicturePath='" + profilePicturePath + '\'' +
                ", isSeller=" + isSeller +
                '}';
    }
}
