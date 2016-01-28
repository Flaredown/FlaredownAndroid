package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 23/09/2015.
 */
public class ViewPagerFragmentBase extends Fragment {
    private EditText editTextFocus;

    protected List<EntryParsers.CollectionCatalogDefinition> collectionCatalogDefinitions;

    public List<EntryParsers.CollectionCatalogDefinition> getCollectionCatalogDefinitions() {
        return collectionCatalogDefinitions;
    }

    public static int indexOfQuestionsPage(String catalogue, String question, List<ViewPagerFragmentBase> fragments) {
        List<Integer> catalogIndexes = indexesOfCatalog(catalogue, fragments);
        for (Integer catalogIndex : catalogIndexes) {
            ViewPagerFragmentBase fragment = fragments.get(catalogIndex);
            if(fragment instanceof CheckinCatalogQFragment) {
                CheckinCatalogQFragment checkinCatalogQFragment = (CheckinCatalogQFragment) fragment;
                int questionIndex = checkinCatalogQFragment.indexOfQuestion(question);
                if(questionIndex != -1)
                    return catalogIndex;
            }
        }
        return -1;
    }

    public static List<Integer> indexesOfCatalog(String catalog, List<ViewPagerFragmentBase> fragments) {
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            ViewPagerFragmentBase fragment = fragments.get(i);
            if(fragment.collectionCatalogDefinitions.size() > 0 && fragment.collectionCatalogDefinitions.get(0).getCatalog().equals(catalog)) {
                integers.add(i);
            }
        }
        return integers;
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
