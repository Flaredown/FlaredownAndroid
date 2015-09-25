package com.flaredown.flaredownApp;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
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

import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class Checkin_catalogQ_fragment extends ViewPagerFragmentBase {
    private static final String DEBUG_KEY = "checkin_catalogQ_fragment";
    JSONArray questions;
    String catalogue;
    int section;
    public Context context;
    private View fragmentRoot;
    private TextView tv_catalogName;
    private TextView tv_sectionTitle;

    private View focusedView;
    private LinearLayout ll_questionHolder;
    public Checkin_catalogQ_fragment() {
    }

    public Checkin_catalogQ_fragment setQuestion(JSONArray question, int section, String catalogue) {
        this.questions = question;
        this.catalogue = catalogue;
        this.section = section;
        this.focusedView = null;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        fragmentRoot = inflater.inflate(R.layout.fragment_checkin_catalog_q, container, false);

        ll_questionHolder = (LinearLayout) fragmentRoot.findViewById(R.id.ll_questionHolder);

        tv_catalogName = (TextView) fragmentRoot.findViewById(R.id.tv_catalog);
        tv_sectionTitle = (TextView) fragmentRoot.findViewById(R.id.tv_question);

        tv_sectionTitle.setText(Locales.read(getActivity(), "catalogs." + catalogue + ".section_" + section + "_prompt").resultIfUnsuccessful("--").createAT());
        tv_catalogName.setText(Locales.read(getActivity(), "catalogs." + catalogue + ".catalog_description").resultIfUnsuccessful(catalogue).createAT());

        try {
            for(int i = 0; i < questions.length(); i++) {
                JSONObject question = questions.getJSONObject(i);
                appendQuestion(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return fragmentRoot;
    }

    private void appendQuestion(JSONObject question) throws JSONException{
        String kind = question.getString("kind");
        if(kind.equals("select")) {
            SelectQuestionInflate selectQuestionInflate = new SelectQuestionInflate(question, catalogue, section);
            ll_questionHolder.addView(selectQuestionInflate.ll_root);
        } else if(kind.equals("number")) {
            NumberQuestionInflate numberQuestionInflate = new NumberQuestionInflate(question, catalogue, section);
            ll_questionHolder.addView(numberQuestionInflate.ll_root);
        } else if(kind.equals("checkbox")){
            CheckBoxQuestionInflate checkBoxQuestionInflate = new CheckBoxQuestionInflate(question, catalogue, section);
            ll_questionHolder.addView(checkBoxQuestionInflate.ll_root);
        } else {
            BlankQuestion blankQuestion = new BlankQuestion(question, catalogue, section);
            ll_questionHolder.addView(blankQuestion.ll_root);
        }
    }

    private class NumberQuestionInflate extends BlankQuestion {
        public NumberQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);

            JSONObject inputs = question.getJSONArray("inputs").getJSONObject(0);

            final EditText editText = new EditText(context);
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
    }
    private class SelectQuestionInflate extends BlankQuestion {
        public SelectQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);

            JSONArray inputs = question.getJSONArray("inputs");
            Checkin_Selector_View checkin_selector_view = new Checkin_Selector_View(getActivity()).setInputs(inputs);
            checkin_selector_view.setButtonClickListener(new Checkin_Selector_View.OnButtonClickListener() {
                @Override
                public void onClick() {
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
        }
    }
    private class CheckBoxQuestionInflate extends BlankQuestion {
        public CheckBoxQuestionInflate(JSONObject question, String catalogue, int section) throws JSONException {
            super(question, catalogue, section);


            Button button = new Button(new ContextThemeWrapper(context, R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button);

            Spanned label = Locales.read(context, "catalogs." + catalogue + "." + question.getString("name")).resultIfUnsuccessful(question.getString("name")).createAT();

            button.setText(label);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                }
            });

            this.ll_root.addView(button);
            int margins  = (int) Styling.getInDP(context, 5);
            ((ViewGroup.MarginLayoutParams) button.getLayoutParams()).setMargins(margins, margins, margins, margins);
        }
    }


    private class BlankQuestion {
        public LinearLayout ll_root;

        BlankQuestion(JSONObject question, String catalogue, int section) throws JSONException{
            // Create root elements
            ll_root = (LinearLayout) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.checkin_question_blank, null);
        }
    }




    private void changeQuestion () {
        if(catalogue == "symptoms") {
            //tv_question.setText();
        }
    }
}
