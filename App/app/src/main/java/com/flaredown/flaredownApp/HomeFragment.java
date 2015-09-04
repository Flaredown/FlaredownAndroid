package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private Button butt_logout;
    private FlareDownAPI flareDownAPI;
    private Context mContext;
    private View fragmentRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        fragmentRoot = inflater.inflate(R.layout.fragment_checkout, container, false);
        flareDownAPI = new FlareDownAPI(mContext);
        butt_logout = (Button) fragmentRoot.findViewById(R.id.butt_logout);
        butt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flareDownAPI.users_sign_out(new FlareDownAPI.OnApiResponse() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(FlareDownAPI.API_Error error) {
                        Toast.makeText(mContext, "Error signing out", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return fragmentRoot;
    }
}
