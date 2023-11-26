package edu.uga.cs.shoppinglist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all items.
 */
public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ListItemHolder> {

    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";

    private List<ListItem> itemsList;
    private Context context;

    public ShoppingListRecyclerAdapter( List<ListItem> itemList, Context context ) {
        this.itemsList = itemList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ListItemHolder extends RecyclerView.ViewHolder {

        TextView item;
        TextView price;
        TextView purchased;

        public ListItemHolder(View itemView ) {
            super(itemView);

            item = itemView.findViewById( R.id.itemName );
        }
    }

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.list_item, parent, false );
        return new ListItemHolder( view );
    }

    // This method fills in the values of the Views to show an item
    @Override
    public void onBindViewHolder( ListItemHolder holder, int position ) {
        ListItem listItem = itemsList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + listItem );

        String item = listItem.getItem();

        holder.item.setText( listItem.getItem());

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditItemDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
//                EditItemDialogFragment editJobFragment =
//                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, company, phone, url, comments );
//                editJobFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}
