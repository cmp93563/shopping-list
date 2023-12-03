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

public class CheckoutDialogFragment extends DialogFragment {

    private EditText etTotal;

    private ShoppingListFragment hostFragment;

    public interface CheckoutDialogListener {
        //void addListItem(ListItem listItem);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fragment_checkout_dialog,
                getActivity().findViewById(R.id.root));

        // get the view objects in the AlertDialog
        etTotal = layout.findViewById(R.id.editTextPrice);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle("Checkout");

        // Provide the negative button listener
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton(android.R.string.ok, new CheckoutListener());

        return builder.create();
    }

    private class CheckoutListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            double total = Double.parseDouble(etTotal.getText().toString());
            hostFragment.addPurchase(total);

            // close the dialog
            dismiss();
        }
    }

    public void setHostFragment( ShoppingListFragment hostFragment )
    {
        this.hostFragment = hostFragment;
    }

}
