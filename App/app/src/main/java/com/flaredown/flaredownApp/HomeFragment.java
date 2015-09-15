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
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.API;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private Button butt_logout;
    private API flareDownAPI;
    private Context mContext;
    private View fragmentRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        fragmentRoot = inflater.inflate(R.layout.fragment_checkout, container, false);

        TextView tv = (TextView) fragmentRoot.findViewById(R.id.textView);
        TextView tv2 = (TextView) fragmentRoot.findViewById(R.id.textView2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).nextQuestion();
            }
        });


        return fragmentRoot;
    }
}
