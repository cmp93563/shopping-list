package edu.uga.cs.shoppinglist;

import static edu.uga.cs.shoppinglist.EditItemDialogFragment.SAVE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all items in the shopping list.
 */

public class ShoppingListRecyclerAdapter
        extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";
    public static final String TAG = "ShoppingListRecyclerAdapter";

    private final Context context;

    private List<ListItem> values;
    private List<ListItem> originalValues;

    ShoppingListFragment hostFragment;

    public ShoppingListRecyclerAdapter( Context context, List<ListItem> itemsList, ShoppingListFragment hostFragment ) {
        this.context = context;
        this.values = itemsList;
        this.originalValues = new ArrayList<ListItem>( itemsList );
        this.hostFragment = hostFragment;
    }

    // reset the originalValues to the current contents of values
    public void sync()
    {
        originalValues = new ArrayList<ListItem>( values );
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        //String price;
        Button checkout;
        Button editBtn;

        public ItemHolder( View itemView ) {
            super( itemView );

            itemName = itemView.findViewById( R.id.itemName );
            checkout = itemView.findViewById( R.id.addToCart );
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.list_item, parent, false );
        return new ItemHolder( view );
    }

    // This method fills in the values of a holder to show a ListItem.
    // The position parameter indicates the position on the list of items list.
    @Override
    public void onBindViewHolder(ItemHolder holder, @SuppressLint("RecyclerView") int position ) {

        ListItem listItem = values.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + listItem );
        //Log.d( DEBUG_TAG, "onBindViewHolder: " + listItem.getPrice() );

        String key = listItem.getKey();
        String item = listItem.getItem();
        //String price = Double.toString(listItem.getPrice());

        holder.itemName.setText( listItem.getItem());
        if (listItem.getInCart()) holder.checkout.setText("Remove From Cart");
        //change color of button

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialogFragment newFragment = new AddItemDialogFragment();
                newFragment.setHostFragment(hostFragment);
                Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                EditItemDialogFragment editItemFragment =
                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item);
                editItemFragment.setHostFragment(hostFragment);
                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });

        holder.checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkout.getText().equals("Add To Cart")) {
                    holder.checkout.setText("Remove From Cart");
                    listItem.setInCart(true);
                    try {
                        /*
                        EditItemDialogFragment newFragment = new EditItemDialogFragment();
                        newFragment.setHostFragment(hostFragment);
                        Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                        Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                        EditItemDialogFragment editItemFragment =
                                EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item);
                        editItemFragment.setHostFragment(hostFragment);
                        editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
                        */
                    } catch (Exception e) {
                        Log.e("RECENT PURCHASES", e.getMessage());
                    }
                } else {
                    listItem.setInCart(false);
                    //listItem.setPrice(-1);
                    hostFragment.updateItem(position, listItem, SAVE);
                    holder.checkout.setText("Add To Cart");
                }
                Log.d(DEBUG_TAG, "item in cart?: " + listItem.getInCart());
            }
        });
    }

    @Override
    public int getItemCount() {
        if( values != null )
            return values.size();
        else
            return 0;
    }
}
