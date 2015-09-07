package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


public class NavigationFragment extends Fragment {

    private ListView mainListView;
    private ArrayAdapter<String> mAdapter;
    View fragmentRoot;
    private CustomAdapterItem[] mTitles = {
            new CustomAdapterItem("Check In", R.drawable.ic_assignment_turned_in_black_24dp),
            new CustomAdapterItem("Discussion", R.drawable.ic_public_black_24dp),
            new CustomAdapterItem("Profile", R.drawable.ic_person_black_24dp),
            new CustomAdapterItem("Settings", R.drawable.ic_setting_light),
            new CustomAdapterItem("Log Out", R.drawable.ic_lock_open_black_24dp)
    };
    private static final int CAI_CHECK_IN = 0;
    private static final int CAI_DISCUSSION = 1;
    private static final int CAI_ACCOUNT_SETTINGS = 2;
    private static final int CAI_SETTINGS = 3;
    private static final int CAI_LOG_OUT = 4;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRoot =  inflater.inflate(R.layout.fragment_navigation, container, false);

        mainListView = (ListView) fragmentRoot.findViewById(R.id.mainListView);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case CAI_LOG_OUT:
                        //DO LOGOUT
                        FlareDownAPI flareDownAPI = new FlareDownAPI(getActivity());
                        flareDownAPI.users_sign_out(new FlareDownAPI.OnApiResponse() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onFailure(FlareDownAPI.API_Error error) {
                                Toast.makeText(getActivity(), "Failed to logout", Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                }
            }
        });

        addDrawerItems();

        return fragmentRoot;
    }
    private void addDrawerItems() {
        //mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mTitles);
        CustomAdapter cus = new CustomAdapter(getActivity(), mTitles);
        mainListView.setAdapter(cus);
    }


    public class CustomAdapterItem {
        public String title = "";
        public int icon;
        public boolean iconSet = false;
        public CustomAdapterItem(String title) {
            this.title = title;
        }
        public CustomAdapterItem (String title, int icon) {
            this.title = title;
            this.icon = icon;
            this.iconSet = true;
        }
    }
    public class CustomAdapter extends BaseAdapter {
        CustomAdapterItem items[];
        LayoutInflater mInflater;

        public CustomAdapter(Context context, CustomAdapterItem[] items) {
            mInflater = LayoutInflater.from(context);
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_navigation_item, parent, false);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.textView1);
                holder.iv = (ImageView) convertView.findViewById(R.id.imageView1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(items[position].title);
            if(items[position].iconSet)
                holder.iv.setImageDrawable(ContextCompat.getDrawable(getActivity(), items[position].icon));
            return convertView;
        }
        class ViewHolder {
            ImageView iv;
            TextView tv;
        }
    }
}
