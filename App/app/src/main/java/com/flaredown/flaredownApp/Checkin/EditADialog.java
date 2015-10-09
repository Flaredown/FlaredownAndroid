package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by thunter on 08/10/2015.
 */
public class EditADialog extends DialogFragment {
    Activity context;
    JSONArray ja_items;
    String title;
    boolean created = false;

    public void setItems(JSONArray items, String title){
        this.ja_items = items;
        this.title = title;
        this.created = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(!created){
            builder.setTitle(Locales.read(getActivity(), "nice_errors.general_error").create());
            builder.setMessage(Locales.read(getActivity(), "nice_errors.general_error_description").create());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            return builder.create();
        }

        builder.setTitle(title);

        ScrollView scrollViewRoot = new ScrollView(getActivity());

        LinearLayout linearLayoutRoot = new LinearLayout(getActivity());
        linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);
        scrollViewRoot.addView(linearLayoutRoot);

        try {
            for (int i = 0; i < ja_items.length(); i++) {
                TextView textView = new TextView(getActivity());
                textView.setText(ja_items.getJSONObject(i).getString("name"));
                linearLayoutRoot.addView(textView);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        builder.setView(scrollViewRoot);








        builder.setPositiveButton(Locales.read(getActivity(), "nav.done").createAT(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });



        return builder.create();
    }

    public class EditADialogException extends Exception {
        public EditADialogException() { super(); }

        public EditADialogException(String detailMessage) {
            super(detailMessage);
        }

    }
}
