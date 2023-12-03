package edu.uga.cs.shoppinglist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class EditPurchaseDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item

    private EditText costView;

    int position;     // the position of the edited ListItem on the list of items
    String key;
    String total;
    List<ListItem> itemsList;
    String roommate;
    String date;

    private RecentPurchasesFragment hostFragment;
    public interface EditPurchaseDialogListener {
        void updateItem(int position, Purchase purchase, int action);
    }

    public static EditPurchaseDialogFragment newInstance(int position, String key, List<ListItem> itemsList, String total, String roommate, String date) {
        EditPurchaseDialogFragment dialog = new EditPurchaseDialogFragment();
        // Supply item values as an argument.
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putInt("position", position);
        args.putParcelableArrayList("itemsList", new ArrayList<>(itemsList));
        args.putString("date", date);
        args.putString("total", total);
        args.putString("roommate", roommate);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        key = getArguments().getString("key");
        position = getArguments().getInt("position");
        total = getArguments().getString("total");
        itemsList = getArguments().getParcelableArrayList("itemsList");
        roommate = getArguments().getString("roommate");
        date = getArguments().getString("date");


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fragment_edit_purchase_dialog, getActivity().findViewById(R.id.root));

        costView = layout.findViewById(R.id.total);
        //priceView = layout.findViewById(R.id.price);

        // Pre-fill the edit texts with the current values for this item.
        // The user will be able to modify them.
        costView.setText(total);
        //priceView.setText(price);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle("Edit Purchase");

        // The Cancel button handler
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton("SAVE", new EditPurchaseDialogFragment.SaveButtonClickListener());

        // The Delete button handler
        builder.setNeutralButton("DELETE", new EditPurchaseDialogFragment.DeleteButtonClickListener());

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String total = costView.getText().toString();
            Purchase purchase = new Purchase();
            purchase.setKey(key);
            purchase.setItems(itemsList);
            purchase.setRoommate(roommate);
            purchase.setDate(date);
            purchase.setTotal(Double.parseDouble(total));
            hostFragment.updateItem(position, purchase, SAVE);
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Purchase purchase = new Purchase();
            purchase.setKey(key);
            purchase.setItems(itemsList);
            purchase.setRoommate(roommate);
            purchase.setDate(date);
            purchase.setTotal(Double.parseDouble(total));
            hostFragment.updateItem(position, purchase, DELETE);
            dismiss();
        }
    }

    public void setHostFragment(RecentPurchasesFragment hostFragment) {
        this.hostFragment = hostFragment;
    }
}