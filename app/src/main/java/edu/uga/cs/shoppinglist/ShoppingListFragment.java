package edu.uga.cs.shoppinglist;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ShoppingListFragment extends Fragment
        implements AddItemDialogFragment.AddListItemDialogListener {


    public static final String TAG = "ShoppingList";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;

    private FirebaseDatabase database;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_shopping_list, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        super.onCreate(savedInstanceState);
        recyclerView = getView().findViewById(R.id.recyclerView);


        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingList");
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    ListItem jobLead = postSnapshot.getValue(ListItem.class);
                    jobLead.setKey( postSnapshot.getKey() );
                    itemsList.add( jobLead );
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }

    public void addListItem(ListItem listItem) {
        // add the new job lead
        // Add a new element (JobLead) to the list of job leads in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingList");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new job lead.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain job leads.
        myRef.push().setValue(listItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Reposition the RecyclerView to show the JobLead most recently added (as the last item on the list).
                // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                // reposition the item into view (show the last item on the list).
                // the post method adds the argument (Runnable) to the message queue to be executed
                // by Android on the main UI thread.  It will be done *after* the setAdapter call
                // updates the list items, so the repositioning to the last item will take place
                // on the complete list of items.
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(itemsList.size() - 1);
                    }
                });

                // Show a quick confirmation
//                Toast.makeText(getApplicationContext(), "List item created for " + listItem.getItem(), Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed to create a Job lead for " + listItem.getItem(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater ) {
//        // inflate the menu
//        inflater.inflate( R.menu.search_menu, menu );
//
//        // Get the search view
//        MenuItem searchMenu = menu.findItem( R.id.appSearchBar );
//        SearchView searchView = (SearchView) searchMenu.getActionView();
//
//        // Provide a search hint
//        searchView.setQueryHint( "Search words" );
//
//        // Chanage the background, text, and hint text colors in the search box
//        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text );
//        searchEditText.setBackgroundColor( getResources().getColor( R.color.white ) );
////        searchEditText.setTextColor( getResources().getColor( R.color.colorPrimaryDark ) );
////        searchEditText.setHintTextColor( getResources().getColor( R.color.colorPrimary ) );
//
//        // Set the listener for the search box
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit( String query ) {
//                Log.d( TAG, "Query submitted" );
//                return false;
//            }
//
//            // This method will implement an incremental search for the search words
//            // It is called every time there is a change in the text in the search box.
//            @Override
//            public boolean onQueryTextChange( String newText ) {
////                recyclerAdapter.getFilter().filter( newText );
//                return true;
//            }
//        });
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }

}