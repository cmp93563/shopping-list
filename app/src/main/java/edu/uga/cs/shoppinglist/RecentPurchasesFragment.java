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

    public static final String TAG = "RecentPurchasesFragment";
    public static final String DEBUG_TAG = "RecentPurchasesFragment";

    private RecyclerView recyclerView;
    private RecentPurchasesRecyclerAdapter recyclerAdapter;
//    private PurchasedItemsRecyclerAdapter recyclerAdapter;

//    private List<ListItem> itemsList;
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
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our item list.
                purchasesList.clear(); // clear the current content; this is inefficient!
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Purchase purchase = postSnapshot.getValue(Purchase.class);
                    purchase.setKey(postSnapshot.getKey());
                    purchasesList.add(purchase);
                    for (ListItem li : purchase.getItems()) {
                        Log.d("RECENT PURCHASES FRAGMENT", li.getItem());
                    }
                }
//                itemsList.clear(); // clear the current content; this is inefficient!
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    Purchase purchase = postSnapshot.getValue(Purchase.class);
//                    purchase.setKey(postSnapshot.getKey());
//                    for (ListItem li : purchase.getItems()) {
//                        itemsList.add(li);
//                    }
//                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_purchases, container, false);
    }
}