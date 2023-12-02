package edu.uga.cs.shoppinglist;

import android.content.Context;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecentPurchasesRecyclerAdapter extends RecyclerView.Adapter<RecentPurchasesRecyclerAdapter.PurchaseItemHolder>
        implements EditPurchaseDialogFragment.EditPurchaseDialogListener {

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
        String key = purchase.getKey();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_purchases, container, false);
    }

    public void updateItem( int position, String total, int action ) {
        if( action == EditPurchaseDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating job lead at: " + position + "(" + total + ")" );

            // Update the recycler view to show the changes in the updated job lead in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this job lead in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "jobleads" );
                    .child( jobLead.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( total ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated job lead at: " + position + "(" + total + ")" );
//                            Toast.makeText(getActivity(), "Job lead updated for " + total,
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update job lead at: " + position + "(" + total + ")" );
//                    Toast.makeText(getActivity(), "Failed to update " + total,
//                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditPurchaseDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting job lead at: " + position + "(" + total + ")" );

            // remove the deleted job lead from the list (internal list in the App)
            purchasesList.remove( position );

            // Update the recycler view to remove the deleted job lead from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this job lead in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "jobleads" );
//                    .child( jobLead.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted job lead at: " + position + "(" + total + ")" );
//                            Toast.makeText(getApplicationContext(), "Job lead deleted for " + total,
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete job lead at: " + position + "(" + total + ")" );
//                    Toast.makeText(getApplicationContext(), "Failed to delete " + total,
//                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if( values != null )
            return values.size();
        else
            return 0;
    }
}
