package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by thunter on 23/09/2015.
 */
public class ViewPagerFragmentBase extends Fragment {
    private EditText editTextFocus;

    Trackable trackable;


    public static int indexOfTrackableQuestion(String catalogue, String question, List<ViewPagerFragmentBase> fragments) {
        for(int i = 0; i < fragments.size(); i++) {
            ViewPagerFragmentBase fragment = fragments.get(i);
            if(fragment.trackable.catalogue.equals(catalogue) && Arrays.asList(fragment.trackable.questions).indexOf(question) != -1) {
                //Found it
                return i;
            }
        }
        return -1;
    }

    public static int indexOfEndOfCatalogue(String catalogue, List<ViewPagerFragmentBase> fragments) {
        boolean startOfCatalogDetected = false;
        for(int i = 0; i < fragments.size(); i++) {
            ViewPagerFragmentBase fragment = fragments.get(i);
            if(fragment.trackable.catalogue.equals(catalogue)) {
                startOfCatalogDetected = true;
            }
            if(!fragment.trackable.catalogue.equals(catalogue) && startOfCatalogDetected)
                return i;
        }
        return fragments.size();
    }


    public static class Trackable {
        String catalogue;
        String[] questions;
        JSONArray JA_questions = new JSONArray();

        public Trackable(String catalogue, JSONArray questions) throws JSONException{
            this.catalogue = catalogue;
            this.JA_questions = questions;
            createQuestionsStrArr(questions);
        }

        private void createQuestionsStrArr(JSONArray questions) throws JSONException{
            this.questions = new String[questions.length()];
            for(int i = 0; i < questions.length(); i++) {
                JSONObject jo = questions.getJSONObject(i);
                this.questions[i] = jo.getString("name");
            }
        }
    }



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
