package com.flaredown.flaredownApp.Checkin;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.flaredown.com.flaredown.R;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by thunter on 08/10/2015.
 */
public class EditEditablesDialog extends DialogFragment {
    Activity context;
    JSONArray ja_items;
    String title;
    String catalog = "";
    boolean itemSet = false;
    LinearLayout ll_root;
    ScrollView sv_root;
    AlertDialog.Builder alertDialogBuilder;

    public EditEditablesDialog initialize(String title, String catalog) {
        this.title = title;
        this.catalog = catalog;
        return this;
    }

    public void setItems(JSONArray items){
        this.ja_items = items;
        this.itemSet = true;

        assembleDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        sv_root = new ScrollView(getActivity());
        ll_root = new LinearLayout(getActivity());

        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setView(sv_root);
        sv_root.addView(ll_root);

        ll_root.setOrientation(LinearLayout.VERTICAL);


        if(!itemSet){
            ProgressBar progressBar = new ProgressBar(getActivity());
            ll_root.addView(progressBar);
        } else {
            assembleDialog();
        }
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return alertDialogBuilder.create();
    }

    public void assembleDialog () {
        if(ll_root == null) return;
        ll_root.removeAllViews();
        int defaultPadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

        View.OnClickListener deleteButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v instanceof Editable && getActivity() instanceof CheckinActivity){
                    Editable editable = (Editable) v;
                    CheckinActivity checkinActivity = (CheckinActivity) getActivity();
                    List<ViewPagerFragmentBase> questionFragments = checkinActivity.getFragmentQuestions();
                    ll_root.removeView(editable);

                    //Get the index of the question and remove it...
                    int index = ViewPagerFragmentBase.indexOfTrackableQuestion(editable.catalog, editable.name, questionFragments);

                    if(index != -1) {
                        if (questionFragments.get(index) instanceof Checkin_catalogQ_fragment) {
                            Checkin_catalogQ_fragment checkin_catalogQ_fragment = (Checkin_catalogQ_fragment) questionFragments.get(index);
                            checkin_catalogQ_fragment.removeQuestion(editable.name);
                        } else {
                            checkinActivity.getScreenSlidePagerAdapter().removeView(index);
                        }
                    }

                }
            }
        };

        try {
            for (int i = 0; i < ja_items.length(); i++) {
                Editable editable = new Editable(getActivity());
                editable.setCatalog(catalog);
                editable.setName(ja_items.getJSONObject(i).getString("name"));
                editable.setOnDeleteClickListener(deleteButtonClick);
                ll_root.addView(editable);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        TextView addACondition = new TextView(getActivity());

        String localKey = "onboarding.";

        switch (catalog) {
            case "symptoms":
                localKey += "add_symptom_button";
                break;
            case "conditions":
                localKey += "add_condition";
        }

        addACondition.setText("+ " + Locales.read(getActivity(), localKey).create());
        addACondition.setGravity(Gravity.CENTER_HORIZONTAL);
        addACondition.setTextColor(getResources().getColor(R.color.accent));
        ll_root.addView(addACondition);
        sv_root.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
    }

    public class Editable extends RelativeLayout {
        Context context;
        Button bt_name;
        String name;
        String catalog;
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
            this.name = name;
            bt_name.setText(name);
            return this;
        }
        public Editable setCatalog(String catalog) {
            this.catalog = catalog;
            return this;
        }

        public Editable setOnDeleteClickListener(final OnClickListener onClickListener) {
            final Editable t = this;
            bt_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(t);
                }
            });
            return this;
        }

    }
}