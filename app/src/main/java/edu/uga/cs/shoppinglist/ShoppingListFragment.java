package edu.uga.cs.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ShoppingListFragment extends Fragment {
//        implements AddItemDialogFragment.AddListItemDialogListener {

    public static final String TAG = "ShoppingList";

    private RecyclerView recyclerView;
    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;

    private FirebaseDatabase database;
    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
    }
    public void onViewCreated( @NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        Button addItem = getView().findViewById(R.id.addItem);

        addItem.setOnClickListener(v -> {
            AddItemDialogFragment newFragment = new AddItemDialogFragment();
            newFragment.setHostFragment( ShoppingListFragment.this );
            newFragment.show( getParentFragmentManager(), null );
        });

        recyclerView = getView().findViewById( R.id.recyclerView );

        // initialize the Job Lead list
        itemsList = new ArrayList<>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
//        recyclerAdapter = new ShoppingListRecyclerAdapter( itemsList, ShoppingListFragment.this );

        recyclerAdapter = new ShoppingListRecyclerAdapter( getActivity(), itemsList );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingList");
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    ListItem listItem = postSnapshot.getValue(ListItem.class);
                    listItem.setKey( postSnapshot.getKey() );
                    itemsList.add( listItem );
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    public void addListItem(ListItem listItem) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingList");

        myRef.push().setValue(listItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(itemsList.size() - 1);
                    }
                });

                // Show a quick confirmation
//                Toast.makeText(getApplicationContext(), "List item created for " + listItem.getItem(), Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed to create a Job lead for " + listItem.getItem(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}