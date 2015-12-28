package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.API_Error;
import com.flaredown.flaredownApp.FlareDown.Locales;
import com.flaredown.flaredownApp.MainToolbarView;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Styling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEditableActivity extends AppCompatActivity {
    API fdAPI;
    Activity context;

    public static final String TITLE = "title";
    public static final String ENDPOINT = "endpoint";
    public static final String RESULT = "result";
    public static final String SELECTED_TRACKABLES = "selected_trackable";

    //TextView tv_cancelButton;
    //TextView tv_title;
    EditText et_input;
    LinearLayout ll_results;
    ScrollView sv_results;
    ProgressBar pb_loading;
    MainToolbarView mainToolbarView;

    String endpoint = "/symptoms/search";
    String title;
    List<String> selectedTrackables = new ArrayList<>();

    public static void startActivity(Activity context, String title, String endpoint, int requestCode) {
        startActivity(context, title, endpoint, requestCode, new ArrayList<String>());
    }
    public static void startActivity(Activity context, String title, String endpoint, int requestCode, List<String> items) {
        Intent intent = new Intent(context, AddEditableActivity.class);
        intent.putExtra(AddEditableActivity.TITLE, title);
        intent.putExtra(AddEditableActivity.ENDPOINT, endpoint);
        intent.putExtra(AddEditableActivity.SELECTED_TRACKABLES, items.toArray(new String[items.size()]));
        //context.startActivity(intent);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add_a_dialog);
        fdAPI = new API(this);


        //tv_cancelButton = (TextView) findViewById(R.id.tv_cancel_button);
        //tv_title = (TextView) findViewById(R.id.tv_title);
        et_input = (EditText) findViewById(R.id.et_input);
        ll_results = (LinearLayout) findViewById(R.id.ll_results);
        sv_results = (ScrollView) findViewById(R.id.sv_results);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);

        mainToolbarView.getActionBar().getMenu().clear();
        mainToolbarView.setBackButton(true);

        if(!getIntent().hasExtra(TITLE) || !getIntent().hasExtra(ENDPOINT) || !getIntent().hasExtra(SELECTED_TRACKABLES)) {
            finish();
            return;
        }
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(getIntent().getStringExtra(TITLE));
        endpoint = getIntent().getStringExtra(ENDPOINT);
        selectedTrackables = new ArrayList<>(Arrays.asList(getIntent().getStringArrayExtra(SELECTED_TRACKABLES)));



        /*tv_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAutocomplete(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private RequestQueue autocompleteRequestQueue;
    private int autoCompleteRequestToken = 0;
    private int shownAutoCompleteRequestToken = 0;
    private void getAutocomplete(final String text) {
        if(autocompleteRequestQueue != null) autocompleteRequestQueue.stop();
        final int requestToken = ++autoCompleteRequestToken;
        pb_loading.setVisibility(View.VISIBLE);
        Item it = new Item(context, text).setName("\"" + text + "\"").setQuantity(
                Locales.read(context, "onboarding.add_new_condition").capitalize1Char().create()
        );
        if(selectedTrackables.indexOf(text) == -1)
            it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnResult(text);
                }
            });
        else
            it.setReadOnly(true);

        if (ll_results.getChildCount() > 0)
            ll_results.removeViewAt(0);
        ll_results.addView(it, 0);


        if(text.equals("")) {
            shownAutoCompleteRequestToken = requestToken;
            ll_results.removeAllViews();
            pb_loading.setVisibility(View.INVISIBLE);
            return;
        }
        sv_results.scrollTo(0,0);

        try {
            autocompleteRequestQueue = fdAPI.get_json_array(endpoint + "/" + URLEncoder.encode(text, API.CHAR_SET), new API.OnApiResponse<JSONArray>() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    pb_loading.setVisibility(View.INVISIBLE);
                    if (requestToken >= shownAutoCompleteRequestToken) {
                        shownAutoCompleteRequestToken = requestToken;
                        try {
                            if (ll_results.getChildCount() > 0) {
                                View tmp = ll_results.getChildAt(0);
                                ll_results.removeAllViews();
                                ll_results.addView(tmp, 0);
                                if (jsonArray.length() > 0 && jsonArray.getJSONObject(0).getString("name").toLowerCase().equals(text.toLowerCase())) {
                                    tmp.setVisibility(View.GONE);
                                } else {
                                    tmp.setVisibility(View.VISIBLE);
                                }
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                String name = item.getString("name");
                                Item tv = new Item(context, item.getString("name"));
                                if(selectedTrackables.indexOf(name) != -1) {
                                    tv.setReadOnly(true);
                                } else {
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(v instanceof Item) {
                                                Item tv = (Item) v;
                                                returnResult(tv.getValue());
                                            }
                                        }
                                    });
                                }
                                tv.setName(item.getString("name"));
                                tv.setQuantity(item.getInt("count"));

                                ll_results.addView(tv);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(API_Error error) {
                    // new DefaultErrors(context, error);
                }
            });
        } catch(UnsupportedEncodingException e) { e.printStackTrace(); }
    }
    private void returnResult(String name) {
        Intent intent = context.getIntent();
        intent.putExtra(RESULT, name);
        context.setResult(RESULT_OK, intent);
        finish();
    }

    private class Item extends FrameLayout {
        private LinearLayout item;
        private TextView tv_name;
        private TextView tv_quantity;
        private String value = "";
        private boolean readOnly = false;
        public Item(Context mContext, String value) {
            super(mContext);
            item = (LinearLayout) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_add_a_dialog_result_item, null);
            this.addView(item);

            tv_name = (TextView) item.findViewById(R.id.tv_name);
            tv_quantity = (TextView) item.findViewById(R.id.tv_quantity);
            setName("");
            setQuantity("");

            setValue(value);
        }

        public Item setName(String name) {
            tv_name.setText(name);
            return this;
        }
        public Item setQuantity(String quantity) {
            tv_quantity.setText(Styling.capitalise1char(quantity));
            return this;
        }
        public Item setQuantity(int amount) {
            return setQuantity(String.valueOf(amount) + ((amount == 1) ? " user" : " users"));
        }

        public Item setValue(String value) {
            this.value = value;
            return this;
        }
        public String getValue() {
            return this.value;
        }

        public Item setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
            if(readOnly)
                this.setBackgroundColor(getResources().getColor(R.color.readonly_background));
            else
                this.setBackgroundColor(Color.TRANSPARENT);
            return this;
        }
    }
}
