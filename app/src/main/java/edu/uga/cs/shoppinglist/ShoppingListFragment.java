package edu.uga.cs.shoppinglist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
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

public class ShoppingListFragment extends Fragment
        implements AddItemDialogFragment.AddListItemDialogListener,
        EditItemDialogFragment.EditItemDialogListener {
    public static final String DEBUG_TAG = "ShoppingList";

    private RecyclerView recyclerView;
    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;

    private FirebaseDatabase database;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addItem = getView().findViewById(R.id.addItem);

        addItem.setOnClickListener(v -> {
            AddItemDialogFragment newFragment = new AddItemDialogFragment();
            newFragment.setHostFragment(ShoppingListFragment.this);
            newFragment.show(getParentFragmentManager(), null);
        });

        recyclerView = getView().findViewById(R.id.recyclerView);

        // initialize the items list
        itemsList = new ArrayList<>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new ShoppingListRecyclerAdapter(getActivity(), itemsList, ShoppingListFragment.this);
        recyclerView.setAdapter(recyclerAdapter);

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingList");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our items list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ListItem listItem = postSnapshot.getValue(ListItem.class);
                    listItem.setKey(postSnapshot.getKey());
                    itemsList.add(listItem);
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });
    }

    public void updateItem( int position, ListItem listItem, int action) {
        DatabaseReference shoppingListRef = database
                .getReference()
                .child( "ShoppingList" )
                .child( listItem.getKey() );

//        DatabaseReference purchaseRef = database
//                .getReference()
//                .child( "Cart" )
//                .child( listItem.getKey() );
        if( action == EditItemDialogFragment.SAVE && !listItem.getInCart()) {
            Log.d( DEBUG_TAG, "Updating item at: " + position + "(" + listItem.getItem() + ")" );

            // Update the recycler view to show the changes in the updated item in that view
            recyclerAdapter.notifyItemChanged( position );
            shoppingListRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( listItem ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + listItem.getItem() + ")" );
                            Toast.makeText(getActivity(), listItem.getItem() + " updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + listItem.getItem() + ")" );
                    Toast.makeText(getActivity(), "Failed to update " + listItem.getItem(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if( action == EditItemDialogFragment.SAVE && listItem.getInCart()) {
            // Update the recycler view to show the changes in the updated item in that view
            recyclerAdapter.notifyItemChanged( position );
            shoppingListRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( listItem ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + listItem.getItem() + ")" );
                            Toast.makeText(getActivity(), listItem.getItem() + " updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + listItem.getItem() + ")" );
                    Toast.makeText(getActivity(), "Failed to update " + listItem.getItem(),
                            Toast.LENGTH_SHORT).show();
                }
            });
//            Log.d( DEBUG_TAG, "Updating item at: " + position + "(" + listItem.getItem() + ")" );
//
//            // Update the recycler view to show the changes in the updated item in that view
////            recyclerAdapter.notifyItemChanged( position );
//            purchaseRef.addListenerForSingleValueEvent( new ValueEventListener() {
//                @Override
//                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                    dataSnapshot.getRef().setValue( listItem ).addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + listItem.getItem() + ")" );
//                            Toast.makeText(getActivity(), listItem.getItem() + " updated",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled( @NonNull DatabaseError databaseError ) {
//                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + listItem.getItem() + ")" );
//                    Toast.makeText(getActivity(), "Failed to update " + listItem.getItem(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//            Log.d( DEBUG_TAG, "Moving item at: " + position + "(" + listItem.getItem() + ") to purchased" );
//
//            // remove the deleted item from the list (internal list in the App)
//            itemsList.remove( position );
//
//            // Update the recycler view to remove the deleted item from that view
//            recyclerAdapter.notifyItemRemoved( position );
//            shoppingListRef.addListenerForSingleValueEvent( new ValueEventListener() {
//                @Override
//                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "deleted item at: " + position + "(" + listItem.getItem() + ") from shopping list." );
//                            Toast.makeText(getActivity(), "added " + listItem.getItem() + " to cart",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled( @NonNull DatabaseError databaseError ) {
//                    Log.e(DEBUG_TAG, "onCancelled", databaseError.toException());
//                    Log.d( DEBUG_TAG, "failed to delete item at: " + position + "(" + listItem.getItem() + ") from shopping list" );
//                    Toast.makeText(getActivity(), "Failed to add " + listItem.getItem() + " to cart",
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
        } else if ( action == EditItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting item at: " + position + "(" + listItem.getItem() + ")" );
            try {
                itemsList.remove( position );
                recyclerAdapter.notifyItemRemoved( position );
            } catch (Exception e) {
                Log.e("SHOPPING LIST", e.getMessage());
            }


            shoppingListRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted item at: " + position + "(" + listItem.getItem() + ")" );
                            Toast.makeText(getActivity(), "item deleted for " + listItem.getItem(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.e(DEBUG_TAG, "onCancelled", databaseError.toException());
                    Log.d( DEBUG_TAG, "failed to delete item at: " + position + "(" + listItem.getItem() + ")" );
                    Toast.makeText(getActivity(), "Failed to delete " + listItem.getItem(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
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
                Toast.makeText(getActivity(), listItem.getItem() + " added successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to add " + listItem.getItem(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}