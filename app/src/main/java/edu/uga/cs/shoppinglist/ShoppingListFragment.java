package edu.uga.cs.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fragment that displays the actual shopping list
 */
public class ShoppingListFragment extends Fragment
        implements AddItemDialogFragment.AddListItemDialogListener,
        EditItemDialogFragment.EditItemDialogListener {
    public static final String DEBUG_TAG = "ShoppingList";

    public int position = 0;
    private RecyclerView recyclerView;
    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;
    private List<ListItem> purchasedItems;

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
        Button checkout = getView().findViewById(R.id.checkout);

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
        DatabaseReference shoppingListRef = database.getReference("ShoppingList");

        database = FirebaseDatabase.getInstance();
        DatabaseReference purchasedRef = database.getReference("PurchasedList");
        shoppingListRef.addValueEventListener(new ValueEventListener() {

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

        try {
            checkout.setOnClickListener(v -> {
                CheckoutDialogFragment newFragment = new CheckoutDialogFragment();
                newFragment.setHostFragment(ShoppingListFragment.this);
                newFragment.show(getParentFragmentManager(), null);
            });
        } catch (Exception e) {
            Log.e("SHOPPING LIST ERROR", e.getMessage());
        }
    }

    @Override
    public void updateItem(int position, ListItem listItem, int action) {
        DatabaseReference shoppingListRef = database
                .getReference()
                .child("ShoppingList")
                .child(listItem.getKey());

        if (action == EditItemDialogFragment.SAVE && !listItem.getInCart()) {
            Log.d(DEBUG_TAG, "Updating item at: " + position + "(" + listItem.getItem() + ")");

            // Update the recycler view to show the changes in the updated item in that view
            recyclerAdapter.notifyItemChanged(position);
            shoppingListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(listItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DEBUG_TAG, "updated item at: " + position + "(" + listItem.getItem() + ")");
                            Toast.makeText(getActivity(), listItem.getItem() + " updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(DEBUG_TAG, "failed to update item at: " + position + "(" + listItem.getItem() + ")");
                    Toast.makeText(getActivity(), "Failed to update " + listItem.getItem(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (action == EditItemDialogFragment.SAVE && listItem.getInCart()) {
            // Update the recycler view to show the changes in the updated item in that view
            recyclerAdapter.notifyItemChanged(position);
            shoppingListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(listItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DEBUG_TAG, "updated item at: " + position + "(" + listItem.getItem() + ")");
                            Toast.makeText(getActivity(), listItem.getItem() + " updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(DEBUG_TAG, "failed to update item at: " + position + "(" + listItem.getItem() + ")");
                    Toast.makeText(getActivity(), "Failed to update " + listItem.getItem(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (action == EditItemDialogFragment.DELETE) {
            Log.d(DEBUG_TAG, "Deleting item at: " + position + "(" + listItem.getItem() + ")");
            try {
                itemsList.remove(position);
                recyclerAdapter.notifyItemRemoved(position);
            } catch (Exception e) {
                Log.e("SHOPPING LIST", e.getMessage());
            }


            shoppingListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DEBUG_TAG, "deleted item at: " + position + "(" + listItem.getItem() + ")");
                            Toast.makeText(getActivity(), "item deleted for " + listItem.getItem(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(DEBUG_TAG, "onCancelled", databaseError.toException());
                    Log.d(DEBUG_TAG, "failed to delete item at: " + position + "(" + listItem.getItem() + ")");
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

    /**
     * Adds a new purchase
     * @param total total cost of purchase
     */
    public void addPurchase(double total) {
        List<Integer> positions = new ArrayList<>();

        purchasedItems = new ArrayList<>();

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        for (ListItem li : itemsList) {
            if (li.getInCart()) {
                li.setPurchased(true);

                // add purchased item to array list
                purchasedItems.add(li);

                //then delete from shopping list
                DatabaseReference myRef = database.getReference("ShoppingList");
                myRef.child(li.getKey()).removeValue();

                Log.d(DEBUG_TAG, "purchased item: " + li.getItem());

            } // if
        } // for

        //create new Purchase Item
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String roommateName = mAuth.getCurrentUser().getDisplayName();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());

        Purchase purchase = new Purchase(purchasedItems, total, roommateName, date);

        DatabaseReference myRef = database.getReference("PurchasesList");

        myRef.push().setValue(purchase).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (itemsList.size() > 1) recyclerView.smoothScrollToPosition(itemsList.size() - 1);
                    }
                });

                // Show a quick confirmation
                Toast.makeText(getActivity(), "purchase added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to add purchase", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("SHOPPING LIST POS", Integer.toString(position));
        positions.add(position);
        position++;
    }
}