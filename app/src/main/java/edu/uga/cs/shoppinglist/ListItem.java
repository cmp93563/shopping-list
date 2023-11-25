package edu.uga.cs.shoppinglist;

/**
 * POJO class to define and create a shopping list item
 */
public class ListItem {

    private String item;
    private double price;
    private boolean purchased;

    public ListItem() {
        this.item = null;
        this.price = -1;
        this.purchased = false;
    }

    public ListItem(String item, double price, boolean purchased) {
        this.item = item;
        this.price = price;
        this.purchased = purchased;
    }

    // getter and setter methods
}