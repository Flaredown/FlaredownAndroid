package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by thunter on 23/09/2015.
 */
public class ViewPagerFragmentBase extends Fragment {
    private EditText editTextFocus;

    Trackable trackable = null;


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

    public static int indexOfCatalogue(String catalogue, List<ViewPagerFragmentBase> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            if(fragments.get(i).trackable.catalogue.equals(catalogue)) return i;
        }
        return -1;
    }

    public static class Trackable {
        String catalogue;
        String[] questions;
        JSONArray JA_questions = new JSONArray();

        public Trackable() {
            catalogue = "";
            questions = new String[0];
        }

        public Trackable(String catalogue, JSONArray questions) throws JSONException{
            this.catalogue = catalogue;
            this.JA_questions = questions;
            createQuestionsStrArr(questions);
        }

        private void createQuestionsStrArr(JSONArray questions) throws JSONException{
            this.questions = new String[questions.length()];
            for(int i = 0; i < questions.length(); i++) {
                JSONArray ja = questions.getJSONArray(i);
                for(int j = 0; j < ja.length(); j++) {
                    JSONObject jo = ja.getJSONObject(j);
                    this.questions[i] = jo.getString("name");
                }
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

    List<UpdateListener> updateListeners = new ArrayList<>();
    public void addOnUpdateListener(UpdateListener updateListener) {
        updateListeners.add(updateListener);
    }

    /**
     * The number input may not trigger an UpdateListener event when the user changes the value....
     * This will allow the input to trigger an UpdateListener if not done prior.
     */
    public JSONArray activityClosing() {
        return new JSONArray();
    }

    public void removeOnUpdateListener(UpdateListener updateListener) {
        updateListeners.remove(updateListener);
    }
    protected void triggerUpdateListener(JSONObject answer) {
        for (UpdateListener updateListener : updateListeners) {
            updateListener.onUpdate(answer);
        }
    }

    public interface UpdateListener {
        void onUpdate(JSONObject answer);
    }


    public JSONArray getResponse() throws JSONException{
        return null;
    }

    public static JSONObject generateResponseObject(String catalog, String question, Object value) {
        try {
            JSONObject responseObject = new JSONObject();
            responseObject.put("name", question);
            responseObject.put("catalog", catalog);
            responseObject.put("value", value);
            return responseObject;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
}
