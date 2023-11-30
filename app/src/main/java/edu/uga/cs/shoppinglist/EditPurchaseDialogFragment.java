//package edu.uga.cs.shoppinglist;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.DialogFragment;
//
//public class EditPurchaseDialogFragment extends DialogFragment {
//
//    // indicate the type of an edit
//    public static final int SAVE = 1;   // update an existing item
//    public static final int DELETE = 2; // delete an existing item
//
//    private EditText itemView;
//    private EditText priceView;
//
//    int position;     // the position of the edited ListItem on the list of items
//    String key;
//    String item;
//    String price;
//
//    private RecentPurchasesFragment hostFragment;
//    public interface EditItemDialogListener {
//        void updateItem(int position, ListItem listItem, int action);
//    }
//
//    public static EditPurchaseDialogFragment newInstance(int position, String key, String item, String price) {
//        EditPurchaseDialogFragment dialog = new EditPurchaseDialogFragment();
//        // Supply item values as an argument.
//        Bundle args = new Bundle();
//        args.putString("key", key);
//        args.putInt("position", position);
//        args.putString("item", item);
//        args.putString("price", price);
//        dialog.setArguments(args);
//
//        return dialog;
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        key = getArguments().getString("key");
//        position = getArguments().getInt("position");
//        item = getArguments().getString("item");
//
//        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View layout = inflater.inflate(R.layout.fragment_edit_purchase_dialog, getActivity().findViewById(R.id.root));
//
//        itemView = layout.findViewById(R.id.item);
//        //priceView = layout.findViewById(R.id.price);
//
//        // Pre-fill the edit texts with the current values for this item.
//        // The user will be able to modify them.
//        itemView.setText(item);
//        //priceView.setText(price);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
//        builder.setView(layout);
//
//        // Set the title of the AlertDialog
//        builder.setTitle("Edit Item");
//
//        // The Cancel button handler
//        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // close the dialog
//                dialog.dismiss();
//            }
//        });
//
//        // The Save button handler
//        builder.setPositiveButton("SAVE", new EditPurchaseDialogFragment.SaveButtonClickListener());
//
//        // The Delete button handler
//        builder.setNeutralButton("DELETE", new EditPurchaseDialogFragment.DeleteButtonClickListener());
//
//        // Create the AlertDialog and show it
//        return builder.create();
//    }
//
//    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            String itemName = itemView.getText().toString();
//            String price = priceView.getText().toString();
//            ListItem listItem = new ListItem(itemName, false, false);
//            listItem.setKey(key);
//            hostFragment.updateItem(position, listItem, SAVE);
//            dismiss();
//        }
//    }
//
//    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//
//            ListItem listItem = new ListItem(item, false, false);
//            listItem.setKey(key);
//            hostFragment.updateItem(position, listItem, DELETE);
//            dismiss();
//        }
//    }
//
//    public void setHostFragment(RecentPurchasesFragment hostFragment) {
//        this.hostFragment = hostFragment;
//    }
//}