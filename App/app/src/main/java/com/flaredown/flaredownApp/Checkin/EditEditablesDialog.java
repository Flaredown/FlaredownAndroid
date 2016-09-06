package com.flaredown.flaredownApp.Checkin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flaredown.flaredownApp.Main.MainActivity;
import com.flaredown.flaredownApp.R;

import java.util.List;

/**
 * Created by thunter on 08/10/2015.
 */ //TODO make deletions work
public class EditEditablesDialog extends DialogFragment {
    List<String> items;
    String title;
    String catalog = "";
    boolean itemSet = false;
    LinearLayout ll_root;
    ScrollView sv_root;
    AlertDialog.Builder alertDialogBuilder;
    CheckinCatalogQFragment updateFragment;

    public EditEditablesDialog initialize(String title, String catalog) {
        return initialize(title, catalog, null);
    }

    public EditEditablesDialog initialize(String title, String catalog, CheckinCatalogQFragment updateFragment) {
        this.title = title;
        this.catalog = catalog;
        this.updateFragment = updateFragment;
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
                if(v instanceof Editable && getActivity() instanceof MainActivity){
                    final Editable editable = (Editable) v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    String itemName = editable.name;
//                    String dialogTitle = Locales.read(getActivity(), "confirm_short_remove").replace("item", itemName).create();// TODO Upgrade to the new api

//                    builder.setTitle(dialogTitle);
//                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            final CheckinFragment checkinActivity = (CheckinFragment) getActivity();
//                            //editable.progress(true);
//                            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading");
//
//                            // TODO implement api
//                            Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                    builder.setNegativeButton(Locales.read(getActivity(), "nav.cancel").create(), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });

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

//        final String addATrackableTitle = Locales.read(getActivity(), localKey).create(); // TODO Upgrade to the new api
//        addATrackable.setText("+ " + addATrackableTitle);
//        addATrackable.setGravity(Gravity.CENTER_HORIZONTAL);
//        addATrackable.setTextColor(getResources().getColor(R.color.accent));
//        addATrackable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int requestCode = 9987;
//                AddEditableActivity.startActivity(getActivity(), addATrackableTitle.toString(), "/" + catalog + "/search", requestCode, items);
//                if (getActivity() instanceof CheckinFragment) {
//                    final CheckinFragment checkinActivity = (CheckinFragment) getActivity();
//                    checkinActivity.setOnActivityResultListener(new CheckinFragment.OnActivityResultListener() {
//                        @Override
//                        public void onActivityResult(int requestCode, int resultCode, Intent data) {
//                            if (resultCode == Activity.RESULT_OK && data.hasExtra(AddEditableActivity.RESULT)) {
//                                final String name = data.getStringExtra(AddEditableActivity.RESULT);
//                                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading");
//                                // TODO create new trackable.
//                                Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });
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
