package edu.uga.cs.shoppinglist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchasedItemsRecyclerAdapter extends RecyclerView.Adapter<PurchasedItemsRecyclerAdapter.ListItemHolder> {

    public static final String DEBUG_TAG = "PurchasedItemsRecyclerAdapter";
    public static final String TAG = "PurchasedItemsRecyclerAdapter";

    private List<ListItem> values;
    private List<ListItem> originalValues;
    private RecentPurchasesFragment hostFragment;
    private FirebaseDatabase database;
    private String roommate;
    private String total;
    private String date;
    private String key;


    public PurchasedItemsRecyclerAdapter(List<ListItem> itemsList, RecentPurchasesFragment hostFragment, String roommate, String date, String key, String total) {
        this.values = itemsList;
        this.total = total;
        this.date = date;
        this.key = key;
        this.roommate = roommate;
        this.originalValues = new ArrayList<ListItem>(itemsList);
        this.hostFragment = hostFragment;
    }

    // reset the originalValues to the current contents of values
    public void sync() {
        originalValues = new ArrayList<ListItem>(values);
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public static class ListItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        ImageButton trash;

        public ListItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameRP);
            trash = itemView.findViewById(R.id.trash);
        }
    }

    @NonNull
    @Override
    public PurchasedItemsRecyclerAdapter.ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_item, parent, false);
        return new PurchasedItemsRecyclerAdapter.ListItemHolder(view);
    }

    // This method fills in the values of a holder to show an item.
    // The position parameter indicates the position on the list of items.
    @Override
    public void onBindViewHolder(PurchasedItemsRecyclerAdapter.ListItemHolder holder, int position) {
        ListItem listItem = values.get(position);
        Log.d(DEBUG_TAG, "onBindViewHolder: " + listItem.getItem());
        holder.itemName.setText(listItem.getItem());
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                ListItem item = values.get(position);
                DatabaseReference shoppingListRef = database.getReference("ShoppingList");
                item.setPurchased(false);
                item.setInCart(false);
                shoppingListRef.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DEBUG_TAG, "item saved: " + item);
                        // Show a quick confirmation
                        Toast.makeText(hostFragment.getActivity(), item.getItem() + " deleted from purchases", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(hostFragment.getActivity(), item.getItem() + "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference purchaseListRef = database.getReference().child("PurchasesList").child(key);
                Purchase purchase = new Purchase();
                purchase.setDate(date);
                purchase.setItems(values);
                purchase.setRoommate(roommate);
                purchase.setKey(key);
                purchase.setTotal(Double.parseDouble(total));

                if (values.size() == 1) {
                    Log.d(DEBUG_TAG, Integer.toString(values.size()));

                    // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
                    // to maintain purchases.
                    purchaseListRef.addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                            dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d( DEBUG_TAG, "deleted purchase at: " + position + "(" + purchase.getTotal() + ")" );
                                    Toast.makeText(hostFragment.getActivity(), "Purchase deleted for " + purchase.getTotal(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled( @NonNull DatabaseError databaseError ) {
                            Log.d( DEBUG_TAG, "failed to delete purchase at: " + position + "(" + purchase.getTotal() + ")" );
                            Toast.makeText(hostFragment.getActivity(), "Failed to delete " + purchase.getTotal(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    // remove the deleted purchase from the list (internal list in the App)
                } else {
                    purchaseListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().setValue(purchase).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(DEBUG_TAG, "updated purchase at: " + position + "(" + purchase.getTotal() + ")");
                                    Toast.makeText(hostFragment.getActivity(), "Purchase updated for " + purchase.getTotal(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(DEBUG_TAG, "failed to update purchase at: " + position + "(" + purchase.getTotal() + ")");
                            Toast.makeText(hostFragment.getActivity(), "Failed to update " + purchase.getTotal(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                values.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (values != null) return values.size();
        else return 0;
    }


}
