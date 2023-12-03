package edu.uga.cs.shoppinglist;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PurchasedItemsRecyclerAdapter extends RecyclerView.Adapter<PurchasedItemsRecyclerAdapter.ListItemHolder> {

    public static final String DEBUG_TAG = "PurchasedItemsRecyclerAdapter";
    public static final String TAG = "PurchasedItemsRecyclerAdapter";

    private List<ListItem> values;
    private List<ListItem> originalValues;

    public PurchasedItemsRecyclerAdapter(List<ListItem> itemsList ) {
        this.values = itemsList;
        this.originalValues = new ArrayList<ListItem>( itemsList );
    }

    // reset the originalValues to the current contents of values
    public void sync()
    {
        originalValues = new ArrayList<ListItem>( values );
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public static class ListItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;

        public ListItemHolder( View itemView ) {
            super( itemView );
            itemName = itemView.findViewById( R.id.itemNameRP );
        }
    }

    @NonNull
    @Override
    public PurchasedItemsRecyclerAdapter.ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.purchased_item, parent, false );
        return new PurchasedItemsRecyclerAdapter.ListItemHolder( view );
    }

    // This method fills in the values of a holder to show an item.
    // The position parameter indicates the position on the list of items.
    @Override
    public void onBindViewHolder(PurchasedItemsRecyclerAdapter.ListItemHolder holder, int position ) {
        ListItem listItem = values.get( position );
        Log.d( DEBUG_TAG, "onBindViewHolder: " + listItem.getItem() );
        holder.itemName.setText( listItem.getItem());
    }

    @Override
    public int getItemCount() {
        if( values != null )
            return values.size();
        else
            return 0;
    }
}
