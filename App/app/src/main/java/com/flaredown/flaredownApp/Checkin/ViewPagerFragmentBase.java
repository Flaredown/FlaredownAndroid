package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by thunter on 23/09/2015.
 */
public class ViewPagerFragmentBase extends Fragment {
    private EditText editTextFocus;
    public void onPageEnter() {

    }
    public void onPageExit() {

    }
    public void setEditTextFocus(EditText focus) {
        editTextFocus = focus;
    }
    public boolean hasFocusEditText() {
        return editTextFocus != null;
    }
    public void focusEditText() {
        // Show the keyboard if edittext on page
        if(editTextFocus != null) {
            editTextFocus.requestFocus();
            if (editTextFocus instanceof EditText)
                ((EditText) editTextFocus).selectAll();
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editTextFocus, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
