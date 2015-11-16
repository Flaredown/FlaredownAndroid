package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by thunter on 08/10/2015.
 */
public class EditEditablesDialog extends DialogFragment {
    List<String> items;
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

    public void setItems(List<String> items){
        this.items = items;
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

        // Carry out delete action.
        final View.OnClickListener deleteButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v instanceof Editable && getActivity() instanceof CheckinActivity){
                    final Editable editable = (Editable) v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    String itemName = editable.name;
                    String dialogTitle = Locales.read(getActivity(), "confirm_short_remove").replace("item", itemName).create();

                    builder.setTitle(dialogTitle);
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final CheckinActivity checkinActivity = (CheckinActivity) getActivity();
                            //editable.progress(true);
                            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading");

                            checkinActivity.flareDownAPI.delete_trackableByName(editable.catalog, editable.name, new API.OnApiResponse<String>() {
                                @Override
                                public void onFailure(API.API_Error error) {
                                    progressDialog.hide();
                                    new DefaultErrors(checkinActivity, error);
                                }

                                @Override
                                public void onSuccess(String result) {
                                    progressDialog.hide();
                                    List<ViewPagerFragmentBase> questionFragments = checkinActivity.getFragmentQuestions();
                                    ll_root.removeView(editable);
                                    items.remove(editable.name);

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
                            });
                        }
                    });
                    builder.setNegativeButton(Locales.read(getActivity(), "nav.cancel").create(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    Dialog dialog = builder.create();
                    //Styling.styleDialog(dialog);
                    dialog.show();


                    /*
                    */

                }
            }
        };

        for (int i = 0; i < items.size(); i++) {
            Editable editable = new Editable(getActivity());
            editable.setCatalog(catalog);
            editable.setName(items.get(i));
            editable.setOnDeleteClickListener(deleteButtonClick);
            ll_root.addView(editable);
        }

        TextView addATrackable = new TextView(getActivity());

        String localKey = "onboarding.";

        switch (catalog) {
            case "symptoms":
                localKey += "add_symptom_button";
                break;
            case "conditions":
                localKey += "add_condition";
        }

        final String addATrackableTitle = Locales.read(getActivity(), localKey).create();
        addATrackable.setText("+ " + addATrackableTitle);
        addATrackable.setGravity(Gravity.CENTER_HORIZONTAL);
        addATrackable.setTextColor(getResources().getColor(R.color.accent));
        addATrackable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int requestCode = 9987;
                AddEditableActivity.startActivity(getActivity(), addATrackableTitle.toString(), "/" + catalog + "/search", requestCode, items);
                if (getActivity() instanceof CheckinActivity) {
                    final CheckinActivity checkinActivity = (CheckinActivity) getActivity();
                    checkinActivity.setOnActivityResultListener(new CheckinActivity.OnActivityResultListener() {
                        @Override
                        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                            if (resultCode == Activity.RESULT_OK && data.hasExtra(AddEditableActivity.RESULT)) {
                                final String name = data.getStringExtra(AddEditableActivity.RESULT);
                                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading");
                                checkinActivity.flareDownAPI.create_trackable(catalog, name, new API.OnApiResponse<JSONObject>() {
                                    @Override
                                    public void onFailure(API.API_Error error) {
                                        progressDialog.hide();
                                        new DefaultErrors(getActivity(), error);
                                    }

                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        progressDialog.hide();
                                        Editable newEditable = new Editable(getActivity());
                                        items.add(name);
                                        newEditable.setCatalog(catalog);
                                        newEditable.setName(name);
                                        newEditable.setOnDeleteClickListener(deleteButtonClick);
                                        ll_root.addView(newEditable, ll_root.getChildCount() - 1);

                                        Checkin_catalogQ_fragment newQuestionFragment = new Checkin_catalogQ_fragment();
                                        JSONArray fragmentQuestionJA = new JSONArray();
                                        fragmentQuestionJA.put(Checkin_catalogQ_fragment.getDefaultQuestionJson(name));

                                        newQuestionFragment.setQuestion(fragmentQuestionJA, 1, catalog);

                                        checkinActivity.getScreenSlidePagerAdapter().addView(newQuestionFragment, ViewPagerFragmentBase.indexOfEndOfCatalogue(catalog, checkinActivity.getFragmentQuestions()));
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        ll_root.addView(addATrackable);
        sv_root.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
    }

    public class Editable extends RelativeLayout {
        Context context;
        Button bt_name;
        ProgressBar progressBar;
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
            progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

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
        public void progress(boolean show) {
            if(show) {
                bt_delete.setVisibility(INVISIBLE);
                progressBar.setVisibility(VISIBLE);
            } else {
                bt_delete.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
            }
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
