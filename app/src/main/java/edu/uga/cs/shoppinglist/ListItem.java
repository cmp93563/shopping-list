package edu.uga.cs.shoppinglist;

/**
 * POJO class to define and create a shopping list item
 */
public class ListItem {

    private String key;
    private String item;
    private double price;
    private boolean purchased;

    public ListItem() {
        this.item = null;
        this.price = -1;
        this.purchased = false;
    }

    public ListItem(String item) {
        this.item = item;
        this.price = -1;
        this.purchased = false;
    }
    public ListItem(String item, double price) {
        this.item = item;
        this.price = price;
        this.purchased = false;
    }
    public ListItem(String item, double price, boolean purchased) {
        this.item = item;
        this.price = price;
        this.purchased = purchased;
    }

    public String getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public boolean getPurchased() {
        return purchased;
    }

    public String getKey() {
        return key;
    }
    public void setItem( String item ) {
        this.item = item;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
    public void setKey(String key) {
        this.key = key;
    }

    // getter and setter methods
}
