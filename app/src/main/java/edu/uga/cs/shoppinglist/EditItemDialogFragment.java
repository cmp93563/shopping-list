package edu.uga.cs.shoppinglist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class EditItemDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing job lead
    public static final int DELETE = 2; // delete an existing job lead

    private EditText itemView;

    int position;     // the position of the edited JobLead on the list of job leads
    String key;
    String item;

    private ShoppingListFragment hostFragment;

    // A callback listener interface to finish up the editing of a JobLead.
    // ReviewJobLeadsActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditItemDialogListener {
        void updateItem(int position, ListItem listItem, int action);
    }

    public static EditItemDialogFragment newInstance(int position, String key, String item) {
        EditItemDialogFragment dialog = new EditItemDialogFragment();
        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putInt("position", position);
        args.putString("item", item);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        key = getArguments().getString("key");
        position = getArguments().getInt("position");
        item = getArguments().getString("item");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fragment_add_item_dialog, getActivity().findViewById(R.id.root));

        itemView = layout.findViewById(R.id.editText1);

        // Pre-fill the edit texts with the current values for this job lead.
        // The user will be able to modify them.
        itemView.setText(item);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle("Edit Item");

        // The Cancel button handler
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton("SAVE", new SaveButtonClickListener());

        // The Delete button handler
        builder.setNeutralButton("DELETE", new DeleteButtonClickListener());

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String itemName = itemView.getText().toString();
            ListItem listItem = new ListItem(itemName);
            listItem.setKey(key);
            hostFragment.updateItem(position, listItem, SAVE);
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListItem listItem = new ListItem(item);
            listItem.setKey(key);
            hostFragment.updateItem(position, listItem, DELETE);
            dismiss();
        }
    }

    public void setHostFragment(ShoppingListFragment hostFragment) {
        this.hostFragment = hostFragment;
    }
}