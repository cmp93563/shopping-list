package edu.uga.cs.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * POJO class to define and create a shopping list item
 */
public class ListItem implements Parcelable{

    private String key;
    private String item;
    //private double price;
    private boolean purchased;
    private boolean inCart;

    public ListItem() {
        this.item = null;
        this.purchased = false;
        this.inCart = false;
    }

    public ListItem(String item, boolean purchased, boolean inCart) {
        this.item = item;
        this.purchased = false;
        this.inCart = false;
    }

    public String getItem() {
        return item;
    }

    public boolean getPurchased() {
        return purchased;
    }

    public String getKey() {
        return key;
    }

    public boolean getInCart() { return inCart; }

    public void setItem(String item) {
        this.item = item;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(item);
        dest.writeByte((byte) (purchased ? 1 : 0)); // 1 if true, 0 if false
        dest.writeByte((byte) (inCart ? 1 : 0)); // 1 if true, 0 if false
    }

    // Parcelable.Creator for creating instances from a Parcel
    public static final Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>() {
        @Override
        public ListItem createFromParcel(Parcel in) {
            return new ListItem(in);
        }

        @Override
        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };

    // Constructor that reads from a Parcel
    private ListItem(Parcel in) {
        key = in.readString();
        item = in.readString();
        purchased = in.readByte() != 0; // true if the byte is not 0
        inCart = in.readByte() != 0; // true if the byte is not 0
    }
}
