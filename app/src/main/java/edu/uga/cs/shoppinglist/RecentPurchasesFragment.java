package edu.uga.cs.shoppinglist;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecentPurchasesFragment extends Fragment {

    public static final String DEBUG_TAG = "RECENT PURCHASES FRAGMENT";

    private RecyclerView recyclerView;
    private RecentPurchasesRecyclerAdapter recyclerAdapter;
    private List<Purchase> purchasesList;

    private FirebaseDatabase database;

    public RecentPurchasesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.recyclerViewRP);

        // initialize the Item list
        purchasesList = new ArrayList<>();

        // use a linear layout manager for the recycler view

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecentPurchasesRecyclerAdapter(getActivity(), purchasesList, RecentPurchasesFragment.this);
        recyclerView.setAdapter(recyclerAdapter);

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("PurchasesList");
        Log.d(DEBUG_TAG, "ON VIEW CREATED");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our item list.
                purchasesList.clear(); // clear the current content; this is inefficient!
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Purchase purchase = postSnapshot.getValue(Purchase.class);
                    purchase.setKey(postSnapshot.getKey());
                    purchasesList.add(purchase);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });
    }

    public void updateItem(int position, Purchase listItem, int action) {
        DatabaseReference shoppingListRef = database.getReference("ShoppingList");
        DatabaseReference purchaseListRef = database.getReference().child("PurchasesList").child(listItem.getKey());

        if (action == EditItemDialogFragment.SAVE) {
            Log.d(DEBUG_TAG, "Updating item at: " + position + "(" + listItem.getTotal() + ")");

            // Update the recycler view to show the changes in the updated item in that view
            recyclerAdapter.notifyItemChanged(position);
            purchaseListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(listItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DEBUG_TAG, "updated item at: " + position + "(" + listItem.getTotal() + ")");
                            Toast.makeText(getActivity(), listItem.getTotal() + " updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(DEBUG_TAG, "failed to update item at: " + position + "(" + listItem.getItems() + ")");
                    Toast.makeText(getActivity(), "Failed to update " + listItem.getItems(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (action == EditItemDialogFragment.DELETE) {
            List<ListItem> jobLeadsList = listItem.getItems();
            for (ListItem jobLead : jobLeadsList) {
                jobLead.setInCart(false);
                jobLead.setPurchased(false);
                shoppingListRef.push().setValue(jobLead).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(jobLeadsList.size() - 1);
                            }
                        });

                        Log.d(DEBUG_TAG, "Job lead saved: " + jobLead);
                        // Show a quick confirmation
                        Toast.makeText(getActivity(), "Job lead created for " + jobLead.getItem(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to create a Job lead for " + jobLead.getItem(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        Log.d(DEBUG_TAG, "Deleting item at: " + position + "(" + listItem.getTotal() + ")");
        try {
            purchasesList.remove(position);
            recyclerAdapter.notifyItemRemoved(position);
        } catch (Exception e) {
            Log.e("SHOPPING LIST", e.getMessage());
        }
        purchaseListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DEBUG_TAG, "deleted item at: " + position + "(" + listItem.getTotal() + ")");
                        Toast.makeText(getActivity(), "item deleted for " + listItem.getTotal(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(DEBUG_TAG, "onCancelled", databaseError.toException());
                Log.d(DEBUG_TAG, "failed to delete item at: " + position + "(" + listItem.getTotal() + ")");
                Toast.makeText(getActivity(), "Failed to delete " + listItem.getTotal(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_purchases, container, false);
    }
}