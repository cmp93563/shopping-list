package edu.uga.cs.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all items in the shopping list.
 */

public class ShoppingListRecyclerAdapter
        extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.JobLeadHolder>
        implements Filterable {

    public static final String DEBUG_TAG = "ShoppingListRecyclerAdapter";
    public static final String TAG = "ShoppingListRecyclerAdapter";

    private final Context context;

    private List<ListItem> values;
    private List<ListItem> originalValues;

    ShoppingListFragment hostFragment;

    public ShoppingListRecyclerAdapter( Context context, List<ListItem> jobLeadList, ShoppingListFragment hostFragment ) {
        this.context = context;
        this.values = jobLeadList;
        this.originalValues = new ArrayList<ListItem>( jobLeadList );
        this.hostFragment = hostFragment;
    }

    // reset the originalValues to the current contents of values
    public void sync()
    {
        originalValues = new ArrayList<ListItem>( values );
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public static class JobLeadHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        String price;
        Button checkout;

        public JobLeadHolder( View itemView ) {
            super( itemView );

            itemName = itemView.findViewById( R.id.itemName );
            checkout = itemView.findViewById( R.id.addToCart );
        }
    }

    @NonNull
    @Override
    public JobLeadHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        // We need to make sure that all CardViews have the same, full width, allowed by the parent view.
        // This is a bit tricky, and we must provide the parent reference (the second param of inflate)
        // and false as the third parameter (don't attach to root).
        // Consequently, the parent view's (the RecyclerView) width will be used (match_parent).
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.list_item, parent, false );
        return new JobLeadHolder( view );
    }

    // This method fills in the values of a holder to show a JobLead.
    // The position parameter indicates the position on the list of jobs list.
    @Override
    public void onBindViewHolder( JobLeadHolder holder, int position ) {

        ListItem listItem = values.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + listItem );

        String key = listItem.getKey();
        String item = listItem.getItem();
        String price = "0";

        holder.itemName.setText( listItem.getItem());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AddItemDialogFragment newFragment = new AddItemDialogFragment();
//                newFragment.setHostFragment(hostFragment);
////                newFragment.show(getParentFragmentManager(), null);
//                Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
//                Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
//                EditItemDialogFragment editItemFragment =
//                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item );
//                editItemFragment.setHostFragment(hostFragment);
//                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RP RECYCLER", "CHECKOUT CLICKED");
                try {
                    AddToCartDialogFragment newFragment = new AddToCartDialogFragment();
                    newFragment.setHostFragment(hostFragment);
                    Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                    Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                    AddToCartDialogFragment editItemFragment =
                            AddToCartDialogFragment.newInstance( holder.getAdapterPosition(), key, item, price );
                    editItemFragment.setHostFragment(hostFragment);
                    editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
                } catch (Exception e) {
                    Log.e("RECENT PURCHASES", e.getMessage());
                }
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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ListItem> list = new ArrayList<ListItem>( originalValues );
                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0) {
                    filterResults.count = list.size();
                    filterResults.values = list;
                }
                else{
                    List<ListItem> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for( ListItem jobLead : list ) {
                        // check if either the company name or the comments contain the search string
                        if( jobLead.getItem().toLowerCase().contains( searchStr )) {
                            resultsModel.add( jobLead );
                        }
/*
                        // this may be a faster approach with a long list of items to search
                        if( jobLead.getCompanyName().regionMatches( true, i, searchStr, 0, length ) )
                            return true;

 */
                    }

                    filterResults.count = resultsModel.size();
                    filterResults.values = resultsModel;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                values = (ArrayList<ListItem>) results.values;
                notifyDataSetChanged();
                if( values.size() == 0 ) {
                    Toast.makeText( context, "Not Found", Toast.LENGTH_LONG).show();
                }
            }

        };
        return filter;
    }
}
