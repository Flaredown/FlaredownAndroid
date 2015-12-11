package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spanned;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.Locales;
import com.flaredown.flaredownApp.Styling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class Checkin_catalogQ_fragment extends ViewPagerFragmentBase {
    private static final String DEBUG_KEY = "checkin_catalogQ_fragment";
    private int section;
    private API api;
    private static final String SAVEINSTANCE_DQUESTIONANS = "double question answers";

    private View fragmentRoot;
    private LinearLayout ll_questionHolder;
    private TextView tv_catalogName;
    private TextView tv_sectionTitle;

    private List<BlankQuestion> questionViews = new ArrayList<>();

    private boolean viewCreated = false;
    private boolean questionSet = false;

    public Checkin_catalogQ_fragment setQuestions(JSONArray questions, int section, String catalog) {
        try {
            this.trackable = new Trackable(catalog, questions);
            this.section = section;
            questionSet = true;
            if(viewCreated) createView();
        } catch (JSONException e) { e.printStackTrace(); }
        return this;
    }
    public void removeQuestion(String questionName) {
        int index = indexOfQuestion(questionName);
        if(index != -1) {
            ll_questionHolder.removeView(questionViews.get(index).ll_root);
            questionViews.remove(index);
        }
    }
    public int indexOfQuestion(String questionName) {
        for (int i = 0; i < questionViews.size(); i++) {
            if(questionViews.get(i).getName().equals(questionName)) {
                return i;
            }
        }
        return -1;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        api = new API(getActivity());

        if(fragmentRoot == null) {
            fragmentRoot = inflater.inflate(R.layout.fragment_checkin_catalog_q, container, false);

            ll_questionHolder = (LinearLayout) fragmentRoot.findViewById(R.id.ll_questionHolder);
            tv_catalogName = (TextView) fragmentRoot.findViewById(R.id.tv_catalog);
            tv_sectionTitle = (TextView) fragmentRoot.findViewById(R.id.tv_question);
            viewCreated = true;
            if(questionSet) createView();

        }
        if(savedInstanceState != null) {
            String[] questionAnswers = savedInstanceState.getStringArray(SAVEINSTANCE_DQUESTIONANS);
            for(int i = 0; i< questionAnswers.length && i < questionViews.size(); i++) {
                questionViews.get(i).setValue(questionAnswers[i]);
            }
        }
        return fragmentRoot;
    }

    public void createView() {
        // Set the catalog title.
        switch (trackable.catalogue) {
            case "conditions":
                tv_catalogName.setText(Locales.read(getActivity(), "onboarding.edit_conditions").resultIfUnsuccessful("Edit conditions.").capitalize1Char().createAT());
                break;
            case "symptoms":
                tv_catalogName.setText(Locales.read(getActivity(), "onboarding.edit_symptoms").resultIfUnsuccessful("Edit symptoms.").capitalize1Char().createAT());
                break;
            default:
                tv_catalogName.setText(Locales.read(getActivity(), "catalogs." + trackable.catalogue + ".catalog_description").resultIfUnsuccessful(trackable.catalogue).capitalize1Char().createAT());
                break;
        }
        String sectionTitle = "--";
        try {
            sectionTitle = trackable.JA_questions.getJSONArray(0).getJSONObject(0).getString("name");
            if (trackable.catalogue.equals("symptoms"))
                sectionTitle = Locales.read(getActivity(), "how_active_were_your_symptoms").create();
            else if (trackable.catalogue.equals("conditions"))
                sectionTitle = Locales.read(getActivity(), "how_active_were_your_conditions").create();
            else
                sectionTitle = Locales.read(getActivity(), "catalogs." + trackable.catalogue + ".section_" + section + "_prompt").resultIfUnsuccessful(sectionTitle).create();

        } catch (JSONException e) {
        }
        tv_sectionTitle.setText(sectionTitle);


        try {
            for(int i = 0; i < trackable.JA_questions.length(); i++) {
                JSONArray ja = trackable.JA_questions.getJSONArray(i);
                for(int j = 0; j < ja.length(); j++) {
                    appendQuestion(ja.getJSONObject(j));
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }


        tv_catalogName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String catalog = trackable.catalogue;

                if(catalog.equals("symptoms") || catalog.equals("conditions")) {
                    String title = Locales.read(getActivity(), "onboarding.edit_" + catalog).create();

                    final EditEditablesDialog editEditablesDialog = new EditEditablesDialog();
                    editEditablesDialog.initialize(title, catalog);
                    editEditablesDialog.show(getActivity().getFragmentManager(), "editablesdialog");
                    api.getEditables(catalog, new API.OnApiResponse<List<String>>() {
                        @Override
                        public void onFailure(API.API_Error error) {
                            new DefaultErrors(getActivity(), error);
                        }

                        @Override
                        public void onSuccess(List<String> result) {
                            editEditablesDialog.setItems(result);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] questionAns = new String[questionViews.size()];
        for (int i = 0; i < questionViews.size(); i++) {
            questionAns[i] = questionViews.get(i).getValue();
        }
        outState.putStringArray(SAVEINSTANCE_DQUESTIONANS, questionAns);
    }

    public void appendQuestion(JSONObject question) throws JSONException{
        String kind = question.getString("kind");
        BlankQuestion questionView = null;
        switch (kind) {
            case "select":
                questionView = new SelectQuestionInflate(question, trackable.catalogue, section);
                break;
            case "number":
                questionView = new NumberQuestionInflate(question, trackable.catalogue, section);
                break;
            case "checkbox":
                questionView = new CheckBoxQuestionInflate(question, trackable.catalogue, section);
                break;
        }
        if(questionView != null) {
            ll_questionHolder.addView(questionView.ll_root);
            if(question.has("response") && !question.getString("response").equals("")) {
                questionView.setValue(question.getString("response"));
            }
        }
    }


    private class NumberQuestionInflate extends BlankQuestion {
        EditText editText;
        double oldValue;
        public NumberQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);
            JSONObject inputs = question.getJSONArray("inputs").getJSONObject(0);

            editText = new EditText(getActivity());
            if(inputs.has("step") && inputs.getString("step").contains("."))
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            else
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            editText.setGravity(Gravity.CENTER_HORIZONTAL);

            if(inputs.has("value")) {
                editText.setText(inputs.getString("value"));
            }

            this.ll_root.addView(editText);
            //if(focusedView == null) focusedView = editText;

            // Make sure it is the first quesiton which is focused
            if(!hasFocusEditText()) setEditTextFocus(editText);
        }

        @Override
        public String getValue() {
            return editText.getText().toString();
        }

        @Override
        public void setValue(String value) {
            try {
                setValue(Double.parseDouble(value));
            } catch (NumberFormatException e) {}
        }
        public void setValue(Double value) {
            oldValue = value;
            editText.setText(String.valueOf(value));
        }
    }
    private class SelectQuestionInflate extends BlankQuestion {
        Checkin_Selector_View checkin_selector_view;
        public SelectQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);

            JSONArray inputs = question.getJSONArray("inputs");
            checkin_selector_view = new Checkin_Selector_View(getActivity()).setInputs(inputs);
            this.ll_root.addView(checkin_selector_view);
        }

        @Override
        public String getValue() {
            return String.valueOf(checkin_selector_view.getValue());
        }

        @Override
        public void setValue(String value) {
            try {
                setValue(Double.parseDouble(value));
            } catch(NumberFormatException e) {}
        }
        public void setValue(double value) {
            checkin_selector_view.setValue(value);
        }
    }
    private class CheckBoxQuestionInflate extends BlankQuestion {
        Button button;
        public CheckBoxQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);

            button = new Button(new ContextThemeWrapper(getActivity(), R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button);

            Spanned label = Locales.read(getActivity(), "catalogs." + catalogue + "." + question.getString("name")).resultIfUnsuccessful(question.getString("name")).createAT();

            button.setText(label);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                }
            });

            this.ll_root.addView(button);
            int margins  = (int) Styling.getInDP(getActivity(), 5);
            ((ViewGroup.MarginLayoutParams) button.getLayoutParams()).setMargins(margins, margins, margins, margins);
        }

        @Override
        public String getValue() {
            return String.valueOf((button.isSelected())? 1 : 0);
        }

        @Override
        public void setValue(String value) {
            try {
                setValue(Double.parseDouble(value));
            } catch (NumberFormatException e) {

            }
        }
        public void setValue(double value) {
            button.setSelected(value == 1.0);
        }
    }



    private class BlankQuestion {
        public LinearLayout ll_root;
        public TextView tv_question;
        private String name;
        public String getValue() {
            return "";
        }
        public void setValue(String value) {

        }
        public String getName() {
            return name;
        }

        BlankQuestion(JSONObject question, String catalogue, int section) throws JSONException {
            questionViews.add(this); // Add to the list of elements for easy restoration
            name = question.getString("name");
            // Create root elements
            ll_root = (LinearLayout) ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.checkin_question_blank, null);
            tv_question = (TextView) ll_root.findViewById(R.id.tv_question);
            if (catalogue.equals("symptoms") || catalogue.equals("conditions")) {
                tv_question.setText(question.getString("name"));
            } else {
                tv_question.setVisibility(View.GONE);
            }
        }
    }

    public static JSONObject getDefaultQuestionJson(String name) {
        try {
            JSONObject defaultQuestionJson = new JSONObject("{\"name\":\"droopy lips\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}");
            defaultQuestionJson.put("name", name);
            return defaultQuestionJson;
        }
        catch(JSONException e) { e.printStackTrace(); }
        return new JSONObject();
    }
}
