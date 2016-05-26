package com.flaredown.flaredownApp.Checkin;

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
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Toolbars.MainToolbarView;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEditableActivity extends AppCompatActivity{

    private TrackableType trackableType;
    private Calendar lastSearchTime = Calendar.getInstance();
    private TextView tvAddNewTrackableName;
    private LinearLayout llAddNewTrackable;
    private EditText et_input;
    private ProgressBar pb_loading;
    private ListView listView;
    private MainToolbarView mainToolbarView;
    private Communicate api;
    private SearchableAdapter adapter;
    private ArrayList<Searchable> searchableList = new ArrayList<>();
    private Calendar checkinDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_activity_add_dialog);
        et_input = (EditText) findViewById(R.id.et_input);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        tvAddNewTrackableName = (TextView) findViewById(R.id.tv_AddNewConditionName);
        llAddNewTrackable = (LinearLayout) findViewById(R.id.llAddNewCondition);
        listView = (ListView) findViewById(R.id.lvAddTrackable);
        adapter = new SearchableAdapter(getApplicationContext(), searchableList);
        listView.setAdapter(adapter);

        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);

        if (getIntent().hasExtra(FlaredownConstants.ADD_TRACKABLE_TYPE_KEY)){
            trackableType = (TrackableType) getIntent().getExtras().get(FlaredownConstants.ADD_TRACKABLE_TYPE_KEY);
        }

        if (getIntent().hasExtra(FlaredownConstants.CHECKIN_DATE_KEY)){
            checkinDate = (Calendar) getIntent().getExtras().get(FlaredownConstants.CHECKIN_DATE_KEY);
        }

        api = new Communicate(getApplicationContext());

        mainToolbarView.getActionBar().getMenu().clear();
        mainToolbarView.setBackButton(true);

        //Listeners
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    llAddNewTrackable.setVisibility(View.VISIBLE);
                    tvAddNewTrackableName.setText("\"" + s + "\"");
                } else {
                    llAddNewTrackable.setVisibility(View.GONE);
                    tvAddNewTrackableName.setText("");
                }

                if (s.length() > 2){
                    if((Calendar.getInstance().getTimeInMillis() - lastSearchTime.getTimeInMillis()) > FlaredownConstants.ADD_TRACKABLE_SEARCH_TIME_MAX ){ //Only search once per second
                        pb_loading.setVisibility(View.VISIBLE);
                        lastSearchTime.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
                        api.search(s.toString(), trackableType.toString().toLowerCase(), new APIResponse<Search, Error>() {
                            @Override
                            public void onSuccess(Search result) {
                                searchableList.clear();
                                searchableList.addAll(result.getSearchables());
                                adapter.notifyDataSetChanged();
                                pb_loading.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onFailure(Error result) {
                                pb_loading.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0){
                    s.clear();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Searchable selected = searchableList.get(i);
                //Create trackable and pass back to checkin activity
                Trackable trackable = new Trackable(trackableType);
                MetaTrackable meta = new MetaTrackable();
                meta.setName(selected.getName());
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
            }
        });

        llAddNewTrackable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.submitNewTrackable(trackableType, tvAddNewTrackableName.getText().toString() , new APIResponse<MetaTrackable, Error>() {
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
                        intent.putExtra(FlaredownConstants.RETURN_TRACKABLE_KEY,trackable);
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Error result) {
                        Intent intent = getIntent();
                        setResult(RESULT_CANCELED,intent);
                        finish();
                    }
                });
            }
        });

    }

    public class SearchableAdapter extends ArrayAdapter<Searchable>{
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
