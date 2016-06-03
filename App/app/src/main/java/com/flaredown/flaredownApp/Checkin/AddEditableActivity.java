package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.MetaTrackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Trackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Searches.Search;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Searches.Searchable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Tag;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.APIv2.ErrorDialog;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Toolbars.MainToolbarView;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEditableActivity extends AppCompatActivity {
    public static void startActivity(Activity activity, TrackableType resourceType) {
        Intent intent = new Intent(activity,AddEditableActivity.class);
        intent.putExtra(RESOURCE_TYPE_KEY, resourceType);
        activity.startActivityForResult(intent,FlaredownConstants.ADD_TRACKABLE_REQUEST_CODE);
    }


    // Constants
    public final static String RESOURCE_TYPE_KEY = "resource_type";
    public final static String CHECK_IN_DATE_KEY = "check_in_date_key";
    public final static String RETURN_TAG_KEY = "return_tag_key";

    // Views.
    private MainToolbarView mainToolbarView;
    private EditText et_userInput;
    private LinearLayout ll_addNewTrackable;
    private TextView tv_addNewTrackable;
    private ListView lv_suggestions;
    private ProgressBar pb_loading;

    private Communicate api;
    private long lastSuggestionRequest; // Used to throttle the number of requests.
    private TrackableType trackableType;
    private ArrayList<Searchable> suggestionList = new ArrayList<>();
    private SearchableAdapter suggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_activity_add_dialog);

        api = new Communicate(this);

        // Assign views.
        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        et_userInput = (EditText) findViewById(R.id.et_user_input);
        ll_addNewTrackable = (LinearLayout) findViewById(R.id.ll_addNewTrackable);
        tv_addNewTrackable = (TextView) findViewById(R.id.tv_addNewTrackable);
        lv_suggestions = (ListView) findViewById(R.id.lv_suggestions);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        suggestionAdapter = new SearchableAdapter(this, suggestionList);
        lv_suggestions.setAdapter(suggestionAdapter);

        mainToolbarView.getActionBar().getMenu().clear();
        mainToolbarView.setBackButton(true);

        // Check that the required arguments are passed through the intent.
        if(!(getIntent().hasExtra(RESOURCE_TYPE_KEY))) {
            // Should never occur, but just in case close activity.
            finish();
            return;
        }

        trackableType = (TrackableType) getIntent().getSerializableExtra(RESOURCE_TYPE_KEY);


        // Listeners.

        // On input change
        et_userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only show add trackable option when input isn't blank.
                if(s.length() > 0) {
                    ll_addNewTrackable.setVisibility(View.VISIBLE);
                    tv_addNewTrackable.setText("\"" + s + "\"");
                } else {
                    ll_addNewTrackable.setVisibility(View.GONE);
                    tv_addNewTrackable.setText("");
                }

                // Start getting suggestions once the user inputs length is greater than 2
                if(s.length() > 2) {
                    // Prevent over saturation of requests.
                    if(Calendar.getInstance().getTimeInMillis() - lastSuggestionRequest > FlaredownConstants.ADD_TRACKABLE_SEARCH_TIME_MAX) {
                        lastSuggestionRequest = Calendar.getInstance().getTimeInMillis();
                        displaySuggestions(s.toString());
                    } else {
                        // TODO Send request after limit time is complete.
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lv_suggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Searchable selected = suggestionList.get(position);
                if(trackableType.isTrackable()) {
                    //Create trackable and pass back to checkin activity
                    Trackable trackable = new Trackable(trackableType);
                    MetaTrackable meta = new MetaTrackable(trackableType);
                    meta.setName(selected.getName());
                    meta.setId(selected.getId());
                    trackable.setTrackableId(selected.getId());
                    trackable.setColourId(selected.getColor_id());
                    trackable.setCreatedAt(selected.getCreatedAt());
                    trackable.setUpdatedAt(selected.getUpdatedAt());
                    trackable.setMetaTrackable(meta);
                    trackable.setValue("0");
                    Intent intent = getIntent();
                    intent.putExtra(FlaredownConstants.RETURN_TRACKABLE_KEY,trackable);
                    setResult(RESULT_OK,intent);
                    finish();
                } else if(trackableType.equals(trackableType.TAG)) {
                    Intent intent = getIntent();
                    Tag tag = new Tag(selected.getId());
                    MetaTrackable metaTrackable = new MetaTrackable(TrackableType.TAG);
                    metaTrackable.setId(selected.getId());
                    metaTrackable.setName(selected.getName());
                    tag.setMetaTrackable(metaTrackable);

                    intent.putExtra(RETURN_TAG_KEY, tag);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        // Create a new Trackable
        ll_addNewTrackable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trackableType.isTrackable()) {
                    api.submitNewTrackable(trackableType, et_userInput.getText().toString(), new APIResponse<MetaTrackable, Error>() {
                        @Override
                        public void onSuccess(MetaTrackable result) {
                            //Create trackable and pass back to checkin activity
                            Trackable trackable = new Trackable(trackableType);
                            trackable.setTrackableId(result.getId());
                            trackable.setColourId(result.getColorId());
                            trackable.setCreatedAt(result.getCreatedAt());
                            trackable.setUpdatedAt(result.getUpdatedAt());
                            trackable.setMetaTrackable(result);
                            trackable.setValue("0");
                            Intent intent = getIntent();
                            intent.putExtra(FlaredownConstants.RETURN_TRACKABLE_KEY, trackable);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Error result) {
                            new ErrorDialog(AddEditableActivity.this, result).setCancelable(true).show();
                        }
                    });
                } else if(trackableType.equals(TrackableType.TAG)) {
                    api.submitNewTag(et_userInput.getText().toString(), new APIResponse<Tag, Error>() {
                        @Override
                        public void onSuccess(Tag result) {
                            Intent intent = getIntent();
                            intent.putExtra(RETURN_TAG_KEY, result);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Error result) {
                            new ErrorDialog(AddEditableActivity.this, result).setCancelable(true).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Gets suggestions from api and displays them inside the list view.
     * @param input The input string to send to the api.
     */
    private void displaySuggestions(String input) {
        pb_loading.setVisibility(View.VISIBLE);
        api.search(input, trackableType.toString().toLowerCase(), new APIResponse<Search, Error>() {
            @Override
            public void onSuccess(Search result) {
                suggestionList.clear();
                suggestionList.addAll(result.getSearchables());
                suggestionAdapter.notifyDataSetChanged();
                pb_loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Error result) {
                pb_loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    public class SearchableAdapter extends ArrayAdapter<Searchable> {
        public SearchableAdapter(Context context, ArrayList<Searchable> searchables){
            super(context,0,searchables);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Searchable searchable = getItem(position);
            if (convertView == null){
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.checkin_activity_add_a_dialog_result_item,parent,false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView quantity = (TextView) convertView.findViewById(R.id.tv_quantity);

            name.setText(searchable.getName());
            quantity.setText(String.format(getString(R.string.add_trackable_list_view_text,searchable.getUsers_count())));

            return convertView;
        }
    }
}
