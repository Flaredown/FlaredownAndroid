package com.flaredown.flaredownApp;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class Checkin_catalogQ_fragment extends Fragment {

    JSONArray question;
    String catalogue;
    int section;
    public Context context;

    public Checkin_catalogQ_fragment() {
    }
    public Checkin_catalogQ_fragment setQuestion(JSONArray question, int section, String catalogue) {
        this.question = question;
        this.catalogue = catalogue;
        this.section = section;
        return this;
    }

    private API flareDownAPI;
    private Context mContext;
    private View fragmentRoot;
    private TextView tv_question;
    private TextView tv_catalogue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        fragmentRoot = inflater.inflate(R.layout.fragment_checkin_catalog_q, container, false);

        tv_question = (TextView) fragmentRoot.findViewById(R.id.tv_question);
        tv_catalogue = (TextView) fragmentRoot.findViewById(R.id.tv_catalogueName);
        String questionKey = "";
        try {
            questionKey = question.getJSONObject(0).getString("name");
        } catch (JSONException e) {e.printStackTrace();}
        questionKey = Locales.read(getActivity(), "catalogs." + catalogue + ".section_" + section + "_prompt").resultIfUnsuccessful(questionKey).create();
        tv_question.setText(questionKey);
        tv_catalogue.setText(Locales.read(getActivity(), "catalogs." + catalogue + ".catalog_description").resultIfUnsuccessful(catalogue).createAT());


        return fragmentRoot;
    }

    private void changeQuestion () {
        if(catalogue == "symptoms") {
            //tv_question.setText();
        }
    }
}
