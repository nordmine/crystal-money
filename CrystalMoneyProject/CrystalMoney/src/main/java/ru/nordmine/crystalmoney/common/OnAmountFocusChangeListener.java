package ru.nordmine.crystalmoney.common;

import android.view.View;
import android.widget.EditText;

public class OnAmountFocusChangeListener implements View.OnFocusChangeListener {

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            String editTextString = editText.getText().toString();
            if (hasFocus && editTextString.equals("0")) {
                editText.setText("");
            }
            if (!hasFocus && editTextString.isEmpty()) {
                editText.setText("0");
            }
        }
    }

}
