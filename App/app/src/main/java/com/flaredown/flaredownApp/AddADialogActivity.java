package com.flaredown.flaredownApp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.flaredown.com.flaredown.R;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddADialogActivity extends AppCompatActivity {
    API fdAPI;
    Activity context;

    TextView tv_cancelButton;
    EditText et_input;
    LinearLayout ll_results;

    String endpoint = "/symptoms/search";
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add_a_dialog);
        fdAPI = new API(this);


        tv_cancelButton = (TextView) findViewById(R.id.tv_cancel_button);
        et_input = (EditText) findViewById(R.id.et_input);
        ll_results = (LinearLayout) findViewById(R.id.ll_results);


        tv_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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


    private void getAutocomplete(final String text) {
        Item it = new Item(context, text).setName("\"" + text + "\"").setQuantity(
                Locales.read(context, "onboarding.add_new_condition").capitalize1Char().create()
        );
        if(ll_results.getChildCount() > 0)
            ll_results.removeViewAt(0);
        ll_results.addView(it, 0);

        if(text.equals("")) {
            ll_results.removeAllViews();
            return;
        }

        fdAPI.get_json_array(endpoint + "/" + text, new API.OnApiResponseArray() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                try {
                    if(ll_results.getChildCount() > 0) {
                        View tmp = ll_results.getChildAt(0);
                        ll_results.removeAllViews();
                        ll_results.addView(tmp, 0);
                    }
                    
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        Item tv = new Item(context, item.getString("name"));
                        tv.setName(item.getString("name"));
                        tv.setQuantity(item.getInt("count"));
                        ll_results.addView(tv);
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onFailure(API.API_Error error) {

            }
        });
    }

    private class Item extends FrameLayout {
        private LinearLayout item;
        private TextView tv_name;
        private TextView tv_quantity;
        private String value = "";
        public Item(Context context, String value) {
            super(context);
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
    }
}
