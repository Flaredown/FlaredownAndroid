package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.flaredown.com.flaredown.R;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by thunter on 08/10/2015.
 */
public class EditEditablesDialog extends DialogFragment {
    Activity context;
    JSONArray ja_items;
    String title;
    String catalog = "";
    boolean created = false;

    public void setItems(JSONArray items, String title, String catalog){
        this.ja_items = items;
        this.title = title;
        this.created = true;
        this.catalog = catalog;
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

        int defaultPadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

        ScrollView scrollViewRoot = new ScrollView(getActivity());

        LinearLayout linearLayoutRoot = new LinearLayout(getActivity());
        linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);
        scrollViewRoot.addView(linearLayoutRoot);

        try {
            for (int i = 0; i < ja_items.length(); i++) {
                Editable editable = new Editable(getActivity());
                editable.setName(ja_items.getJSONObject(i).getString("name"));
                linearLayoutRoot.addView(editable);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        TextView addACondition = new TextView(getActivity());

        String localKey = "onboarding.";

        switch (catalog) {
            case "symptoms":
                localKey += "add_symptom_button";
                break;
        }

        addACondition.setText("+ " + Locales.read(getActivity(), localKey).create());
        addACondition.setGravity(Gravity.CENTER_HORIZONTAL);
        addACondition.setTextColor(getResources().getColor(R.color.accent));
        linearLayoutRoot.addView(addACondition);

        builder.setView(scrollViewRoot);

        scrollViewRoot.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);







        builder.setPositiveButton(Locales.read(getActivity(), "nav.done").createAT(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });



        return builder.create();
    }

    public class Editable extends RelativeLayout {
        Context context;
        Button bt_name;
        ImageButton bt_delete;

        public Editable(Context context) {
            super(context);
            this.context = context;
            LayoutInflater mInflater = LayoutInflater.from(context);
            mInflater.inflate(R.layout.checkin_editable_item, this, true);

            bt_name = (Button) this.findViewById(R.id.bt_name);
            bt_delete = (ImageButton) this.findViewById(R.id.bt_delete);

            int defaultSmallMargin = context.getResources().getDimensionPixelSize(R.dimen.sep_margin_small);
            this.setPadding(0, 0, 0, defaultSmallMargin);
        }


        public Editable setName(String name){
            bt_name.setText(name);
            return this;
        }

    }
}
