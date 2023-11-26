package edu.uga.cs.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ShoppingList extends AppCompatActivity {


    private RecyclerView recyclerView;
//    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        recyclerView = findViewById(R.id.recyclerView);

        Button addItem = findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddItemDialogFragment();
                newFragment.show(getSupportFragmentManager(), null);
            }
        });
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
                Toast.makeText(getApplicationContext(), "List item created for " + listItem.getItem(), Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to create a Job lead for " + listItem.getItem(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}