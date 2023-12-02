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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecentPurchasesRecyclerAdapter extends RecyclerView.Adapter<RecentPurchasesRecyclerAdapter.PurchaseItemHolder> {

    public static final String DEBUG_TAG = "RECENT PURCHASES RECYCLER ADAPTER";

    private final Context context;

    private List<Purchase> values;
    private List<Purchase> originalValues;
    private RecyclerView.RecycledViewPool
            viewPool
            = new RecyclerView
            .RecycledViewPool();

    private RecentPurchasesFragment hostFragment;
    private PurchasedItemsRecyclerAdapter recyclerAdapter;
    private FirebaseDatabase database;
    RecyclerView recyclerView;

    public RecentPurchasesRecyclerAdapter( Context context, List<Purchase> purchasesList, RecentPurchasesFragment hostFragment ) {
        this.context = context;
        this.values = purchasesList;
        this.originalValues = new ArrayList<Purchase>( purchasesList );
        this.hostFragment = hostFragment;
        Log.d( DEBUG_TAG, "CONSTRUCTOR" );
    }

    // reset the originalValues to the current contents of values
    public void sync()
    {
        originalValues = new ArrayList<Purchase>( values );
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    public static class PurchaseItemHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView purchaseBy;
        TextView totalCost;

        public PurchaseItemHolder( View itemView ) {
            super( itemView );
            Log.d( DEBUG_TAG, "PURCHASE ITEM HOLDER" );

            recyclerView = itemView.findViewById( R.id.recyclerViewItems );
            purchaseBy = itemView.findViewById( R.id.purchaseBy );
            totalCost = itemView.findViewById( R.id.totalCost );
        }
    }

    @NonNull
    @Override
    public RecentPurchasesRecyclerAdapter.PurchaseItemHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.purchase, parent, false );
        return new RecentPurchasesRecyclerAdapter.PurchaseItemHolder( view );
    }

    // This method fills in the values of a holder to show an item.
    // The position parameter indicates the position on the list of items.
    @Override
    public void onBindViewHolder(RecentPurchasesRecyclerAdapter.PurchaseItemHolder holder, int position ) {
        Purchase purchase = values.get( position );
        Log.d( DEBUG_TAG, "onBindViewHolder: " + purchase );
//        String key = purchase.getKey();
        holder.purchaseBy.setText( "Purchase by: " + purchase.getRoommate());
        holder.totalCost.setText( "Total cost: $" + purchase.getTotal());

        List<ListItem> itemsList = new ArrayList<>();

        // use a linear layout manager for the recycler view
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                holder
                        .recyclerView
                        .getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        layoutManager
                .setInitialPrefetchItemCount(
                        purchase
                                .getItems()
                                .size());
        holder.recyclerView.setLayoutManager(layoutManager);
        PurchasedItemsRecyclerAdapter childItemAdapter
                = new PurchasedItemsRecyclerAdapter(purchase.getItems());
        holder
                .recyclerView
                .setLayoutManager(layoutManager);
        holder
                .recyclerView
                .setAdapter(childItemAdapter);
        holder
                .recyclerView
                .setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        if( values != null )
            return values.size();
        else
            return 0;
    }
}
