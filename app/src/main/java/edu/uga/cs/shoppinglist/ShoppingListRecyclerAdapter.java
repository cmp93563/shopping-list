package edu.uga.cs.shoppinglist;

import static edu.uga.cs.shoppinglist.EditItemDialogFragment.SAVE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

    /**
     * This method sets the values of the shopping list
     * @param context
     * @param itemsList
     * @param hostFragment
     */

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

        String key = listItem.getKey();
        String item = listItem.getItem();

        holder.itemName.setText( listItem.getItem());
        if (listItem.getInCart()) holder.checkout.setText("Remove From Cart");
        else holder.checkout.setText("Add To Cart");
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

    /**
     * Retrieves the number of items in the shopping list
     * @return Number of items in the shopping list
     */
    @Override
    public int getItemCount() {
        if( values != null )
            return values.size();
        else
            return 0;
    }
}
