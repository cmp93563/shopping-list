package edu.uga.cs.shoppinglist;

import java.util.ArrayList;
import java.util.List;

public class Purchase {

    //how to reference items purchased?
    private List<String> items;
    private double total;
    private String roommate;

    public Purchase() {
        this.items = null;
        this.total = -1;
        this.roommate = null;
    }

    public Purchase(List<String> items, double total, String roommate) {
        this.items = items;
        this.total = total;
        this.roommate = roommate;
    }

    // getters and setters
}
