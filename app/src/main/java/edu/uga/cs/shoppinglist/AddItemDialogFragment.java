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

// A DialogFragment class to handle item additions from the item review activity
// It uses a DialogFragment to allow the input of a new item.
public class AddItemDialogFragment extends DialogFragment {

    private EditText item;

    private ShoppingListFragment hostFragment;

    public interface AddListItemDialogListener {
        void addListItem(ListItem listItem);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fragment_add_item_dialog,
                getActivity().findViewById(R.id.root));

        // get the view objects in the AlertDialog
        item = layout.findViewById(R.id.editText1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle("Add New Item");
        // Provide the negative button listener
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton(android.R.string.ok, new AddListItemListener());

        return builder.create();
    }

    private class AddListItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // get the new item data from the user
            String itemName = item.getText().toString();

            // create a new ListItem object
            ListItem listItem = new ListItem( itemName, false, false );

            hostFragment.addListItem( listItem );

            // close the dialog
            dismiss();
        }
    }

    public void setHostFragment(ShoppingListFragment hostFragment )
    {
        this.hostFragment = hostFragment;
    }

}
