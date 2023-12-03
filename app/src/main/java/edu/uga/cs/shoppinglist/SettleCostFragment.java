package edu.uga.cs.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SettleCostFragment extends Fragment {

    private String DEBUG_TAG = "SettleCost";

    private double total;
    private TextView totalCost;
    private TextView avgCost;
    private Button settleBtn;
    private DatabaseReference dbRef;
    private DatabaseReference dbRef2;
    private ListView listView;

    public SettleCostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settle_cost, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalCost = getView().findViewById(R.id.textView4);

        dbRef = FirebaseDatabase.getInstance().getReference("PurchasesList");
        total = 0;
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Purchase purchase = postSnapshot.getValue(Purchase.class);
                    purchase.setKey(postSnapshot.getKey());
                    total = total + purchase.getTotal();
                    Log.d(DEBUG_TAG, "total: " + total);
                }
                totalCost.setText("$" + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(DEBUG_TAG, error.getMessage());
            }
        });

        List<String> roommates = new ArrayList<>();
        List<Double> paid = new ArrayList<>();

        avgCost = getView().findViewById(R.id.textView6);
        settleBtn = getView().findViewById(R.id.button5);
        listView = (ListView) getView().findViewById(R.id.listView);

        dbRef2 = FirebaseDatabase.getInstance().getReference("Users");

        settleBtn.setOnClickListener(v -> {

            // get user names into list
            dbRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String name = postSnapshot.getValue().toString();
                        roommates.add(name);
                    }

                    // display average
                    double average = total / roommates.size();
                    avgCost.setText("$" + String.format("%.2f",average));

                    // allocate room for totals paid
                    for (int i = 0; i < roommates.size(); i++) {
                        paid.add(0.0);
                    }

                    // loop through purchases and find totals per roommate
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Purchase purchase = postSnapshot.getValue(Purchase.class);
                                purchase.setKey(postSnapshot.getKey());

                                int index = roommates.indexOf(purchase.getRoommate());
                                double toAdd = paid.get(index) + purchase.getTotal();

                                paid.set(index,toAdd);

                                // remove purchase from dB
                                dbRef.child(purchase.getKey()).removeValue();
                            }

                            //create new array of strings to be displayed
                            String[] lines = new String[roommates.size()];
                            for (int i = 0; i < roommates.size(); i++) {
                                String line = roommates.get(i) + " has spent $" + paid.get(i);
                                lines[i]=line;
                            }

                            // set roommates and totals to display as list
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.roommate_totals, R.id.textView10, lines);
                            listView.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(DEBUG_TAG, "error: " + error.getMessage());
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(DEBUG_TAG, error.getMessage());
                }
            });
        });
    } // onViewCreated
}