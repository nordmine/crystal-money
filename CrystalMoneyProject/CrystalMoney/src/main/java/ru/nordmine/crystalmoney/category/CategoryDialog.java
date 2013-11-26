package ru.nordmine.crystalmoney.category;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import ru.nordmine.crystalmoney.R;

public class CategoryDialog extends DialogFragment {

    public interface OnCategoryRenameListener {
        public void onCategoryRenamed(CategoryItem categoryItem);
    }

    private OnCategoryRenameListener listener;

    public static CategoryDialog newInstance(CategoryItem categoryItem) {
        CategoryDialog dialog = new CategoryDialog();
        Bundle args = new Bundle();
        args.putSerializable("item", categoryItem);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnCategoryRenameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCategoryRenameListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CategoryItem category = (CategoryItem) getArguments().getSerializable("item");

        final AlertDialog.Builder categoryAlert = new AlertDialog.Builder(getActivity());
        final EditText categoryNameEditText = new EditText(getActivity());
        categoryNameEditText.setText(category.getName());
        categoryAlert.setTitle(R.string.caption_rename);
        categoryAlert.setMessage(R.string.caption_category_name);
        categoryAlert.setView(categoryNameEditText);
        categoryAlert.setPositiveButton(R.string.caption_save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = categoryNameEditText.getText().toString().trim();
                        category.setName(value);
                        listener.onCategoryRenamed(category);
                    }
                }
        );
        categoryAlert.setNegativeButton(R.string.caption_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }
        );
        return categoryAlert.create();
    }
}
