package com.flaredown.flaredownApp;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class Checkin_catalogQ_fragment extends Fragment {
    private static final String CURRENT_VALUE = "Current_value";
    private static final String DEBUG_KEY = "checkin_catalogQ_fragment";
    private int UNIQUE_IDS = 400;
    JSONArray questions;
    String catalogue;
    int section;
    public Context context;
    private View fragmentRoot;
    private LinearLayout ll_questionHolder;
    private int questionId = 1;

    public Checkin_catalogQ_fragment() {
    }

    public Checkin_catalogQ_fragment setQuestion(JSONArray question, int section, String catalogue) {
        this.questions = question;
        this.catalogue = catalogue;
        this.section = section;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        fragmentRoot = inflater.inflate(R.layout.fragment_checkin_catalog_q, container, false);

        ll_questionHolder = (LinearLayout) fragmentRoot.findViewById(R.id.ll_questionHolder);

        try {
            if(questions.getJSONObject(0).getString("kind").equals("select")) {
                SelectQuestionInflate selectQuestionInflate = new SelectQuestionInflate(questions.getJSONObject(0), catalogue, section);
                ll_questionHolder.addView(selectQuestionInflate.ll_root);
            } else {
                BlankQuestion blankQuestion = new BlankQuestion(questions.getJSONObject(0), catalogue, section);
                ll_questionHolder.addView(blankQuestion.ll_root);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return fragmentRoot;
    }

    private class SelectQuestionInflate extends BlankQuestion {
        public SelectQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);

            JSONArray inputs = question.getJSONArray("inputs");
            Checkin_Selector_View checkin_selector_view = new Checkin_Selector_View(getActivity()).setInputs(inputs);
            checkin_selector_view.setButtonClickListener(new Checkin_Selector_View.OnButtonClickListener() {
                @Override
                public void onClick() {
                    PreferenceKeys.log(PreferenceKeys.LOG_D, DEBUG_KEY, "SOME BUTTON PRESSED");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Adding a dealy... allowing confirmation of seleciton.
                                Thread.sleep(250);
                                ((HomeActivity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((HomeActivity) context).nextQuestion();
                                    }
                                });

                            } catch (InterruptedException e) { e.printStackTrace(); }
                        }
                    }).start();

                }
            });
            //checkin_selector_view.setId(Styling.getUniqueId());
            //TODO: restore correctly
            checkin_selector_view.setId(R.id.bt_sign_in);

            this.ll_root.addView(checkin_selector_view);


            //Checkin_Selector_View checkin_selector_view = (Checkin_Selector_View) this.ll_root.findViewById(R.id.csv_selector);
            //checkin_selector_view.setInputs(inputs);

        }
    }


    private class BlankQuestion {
        public LinearLayout ll_root;
        public TextView tv_questionTitle;
        public TextView tv_catalog;

        BlankQuestion(JSONObject question, String catalogue, int section) throws JSONException{
            // Create root elements
            ll_root = (LinearLayout) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.checkin_question_blank, null);
            tv_questionTitle = (TextView) ll_root.findViewById(R.id.tv_question);
            tv_catalog = (TextView) ll_root.findViewById(R.id.tv_catalog);
            // Set the questions
            String questionKey = question.getString("name");
            tv_questionTitle.setText(Locales.read(getActivity(), "catalogs." + catalogue + ".section_" + section + "_prompt").resultIfUnsuccessful(questionKey).createAT());
            tv_catalog.setText(Locales.read(getActivity(), "catalogs." + catalogue + ".catalog_description").resultIfUnsuccessful(catalogue).createAT());
        }
    }




    private void changeQuestion () {
        if(catalogue == "symptoms") {
            //tv_question.setText();
        }
    }
}
