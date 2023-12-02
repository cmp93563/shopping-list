package edu.uga.cs.shoppinglist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Purchase {

    //how to reference items purchased?
    private String key;
    private List<ListItem> items;
    private double total;
    private String roommate;

    private String date;

    public Purchase() {
        this.items = null;
        this.total = -1;
        this.roommate = null;
        this.date = null;
    }

    public Purchase(List<ListItem> items, double total, String roommate, String date) {
        this.items = items;
        this.total = total;
        this.roommate = roommate;
        this.date = date;
    }

    // getters and setters

    public String getKey() {
        return key;
    }
    public List<ListItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public String getRoommate() {
        return roommate;
    }

    public String getDate() {
        return date;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setRoommate(String roommate) {
        this.roommate = roommate;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
