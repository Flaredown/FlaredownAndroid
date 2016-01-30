package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.API_Error;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.Locales;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Styling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckinCatalogQFragment extends ViewPagerFragmentBase {
    private static final String DEBUG_KEY = "checkinCatalogQFragment";
    private API api;
    private Integer section;

    /*
    Instant state arguments.
     */
    private static final String SI_ENTRY_JSON = "entry json";
    private static final String SI_RESPONSE_JSON = "response json";
    private static final String SI_SECTION = "section number";

    private View fragmentRoot = null;
    private LinearLayout ll_questionHolder;
    private TextView tv_catalogName;
    private TextView tv_sectionTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        api = new API(getActivity());
        if(savedInstanceState != null && savedInstanceState.containsKey(SI_ENTRY_JSON) && savedInstanceState.containsKey(SI_RESPONSE_JSON) && savedInstanceState.containsKey(SI_SECTION)) {
            try {
                collectionCatalogDefinitions = EntryParsers.getCatalogDefinitions(new JSONObject(savedInstanceState.getString(SI_ENTRY_JSON)), new JSONArray(savedInstanceState.getString(SI_RESPONSE_JSON)));
                section = savedInstanceState.getInt(SI_SECTION);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (fragmentRoot == null){
            assignViews(inflater, container);
            init();
        }
        return fragmentRoot;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (BaseQuestion questionView : questionViews) {
            questionView.getValue(); // Updates response object
        }
        outState.putString(SI_ENTRY_JSON, EntryParsers.getCatalogDefinitionsJSON(collectionCatalogDefinitions).toString());
        outState.putString(SI_RESPONSE_JSON, EntryParsers.getResponsesJSONCatalogDefinitionList(collectionCatalogDefinitions).toString());
        outState.putInt(SI_SECTION, section);
    }

    private void assignViews(LayoutInflater inflater, ViewGroup container) {
        fragmentRoot = inflater.inflate(R.layout.fragment_checkin_catalog_q, container, false);
        ll_questionHolder = (LinearLayout) fragmentRoot.findViewById(R.id.ll_questionHolder);
        tv_catalogName = (TextView) fragmentRoot.findViewById(R.id.tv_catalog);
        tv_sectionTitle = (TextView) fragmentRoot.findViewById(R.id.tv_question);
    }

    private void init() {
        if(collectionCatalogDefinitions != null) {
            //Set the catalog title.
            if(collectionCatalogDefinitions.size() > 0 && collectionCatalogDefinitions.get(0).size() > 0) {
                switch (collectionCatalogDefinitions.get(0).get(0).getCatalog()) {
                    case "conditions":
                    case "symptoms":
                        tv_catalogName.setText(Locales.read(getActivity(), "onboarding.edit_" + collectionCatalogDefinitions.get(0).get(0).getCatalog()).capitalize1Char().createAT());
                        break;
                    default:
                        tv_catalogName.setText(Locales.read(getActivity(), "catalogs." + collectionCatalogDefinitions.get(0).get(0).getCatalog() + ".catalog_description").capitalize1Char().createAT());
                        break;
                }
                //Open edit trackables dialog on catalog title click.
                tv_catalogName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(EntryParsers.catalogDefinitionHasOneElement(collectionCatalogDefinitions)) {
                            String catalog = EntryParsers.getFirstCatalogDefinition(collectionCatalogDefinitions).getCatalog();

                            switch (catalog) {
                                case "symptoms":
                                case "conditions":
                                    String title = Locales.read(getActivity(), "onboarding.edit_" + catalog).create();

                                    final EditEditablesDialog editEditablesDialog = new EditEditablesDialog();
                                    editEditablesDialog.initialize(title, catalog);
                                    editEditablesDialog.show(getActivity().getFragmentManager(), "editablesdialog");
                                    api.getEditables(catalog, new API.OnApiResponse<List<String>>() {
                                        @Override
                                        public void onFailure(API_Error error) {
                                            new DefaultErrors(getActivity(), error);
                                        }

                                        @Override
                                        public void onSuccess(List<String> result) {
                                            editEditablesDialog.setItems(result);
                                        }
                                    });
                                    break;
                            }
                        }
                    }
                });

                //Set the section title.
                String sectionTitle = "--";
                switch (collectionCatalogDefinitions.get(0).get(0).getCatalog()) {
                    case "symptoms":
                        if (questionViews.size() == 0)
                            sectionTitle = Locales.read(getActivity(), "oops_no_symptoms_being_tracked").create();
                        else
                            sectionTitle = Locales.read(getActivity(), "how_active_were_your_symptoms").create();
                        break;
                    case "conditions":
                        if (questionViews.size() == 0)
                            sectionTitle = Locales.read(getActivity(), "oops_no_conditions_being_tracked").create();
                        else
                            sectionTitle = Locales.read(getActivity(), "how_active_were_your_conditions").create();
                        break;
                    default:
                        sectionTitle = Locales.read(getActivity(), "catalogs." + collectionCatalogDefinitions.get(0).get(0).getCatalog() + ".section_" + section + "_prompt").resultIfUnsuccessful(sectionTitle).create();
                        break;
                }
                //sectionTitle = String.valueOf(section);
                tv_sectionTitle.setText(sectionTitle);
            }
            for (EntryParsers.CollectionCatalogDefinition collectionCatalogDefinitions : this.collectionCatalogDefinitions) {
                for (EntryParsers.CatalogDefinition catalogDefinition : collectionCatalogDefinitions) {
                    appendQuesiton(catalogDefinition);
                }
            }
        }
    }

    @Override
    public void onPageExit() {
        for (BaseQuestion questionView : questionViews) {
            questionView.getValue(); // Ensures Catalog Definition Response is upto date.
            if(questionView instanceof NumberQuestion && questionView.getCatalogDefinition().getResponse() != null && questionView.hasValueChanged())
                triggerOnUpdateListener(questionView.getCatalogDefinition());
        }
    }

    private List<BaseQuestion> questionViews = new ArrayList<>();

    public void setQuestions(List<EntryParsers.CollectionCatalogDefinition> collectionCatalogDefinitions, Integer section) {
        this.collectionCatalogDefinitions = collectionCatalogDefinitions;
        this.section = section;
    }

    public void appendQuesiton(EntryParsers.CatalogDefinition catalogDefinition) {
        BaseQuestion questionView = null;
        switch(catalogDefinition.getKind()) { //TODO implement number
            case "select":
                questionView = new SelectQuestion(getActivity(), catalogDefinition);
                break;
            case "checkbox":
                questionView = new CheckBoxQuestion(getActivity(), catalogDefinition);
                break;
            case "number":
                questionView = new NumberQuestion(getActivity(), catalogDefinition);
                break;
        }
        if(questionView != null) {
            if(collectionCatalogDefinitions.size() > 0) { // Add to list if not already there.
                if(collectionCatalogDefinitions.get(0).indexOf(catalogDefinition) == -1) {
                    collectionCatalogDefinitions.get(0).add(catalogDefinition);
                }
            }
            ll_questionHolder.addView(questionView);
            questionViews.add(questionView);
        }
    }

    public void removeQuestion(String name) {
        int location = indexOfQuestion(name);
        for (EntryParsers.CollectionCatalogDefinition collectionCatalogDefinition : collectionCatalogDefinitions) {
            for (EntryParsers.CatalogDefinition catalogDefinition : collectionCatalogDefinition) {
                if(name.equals(catalogDefinition.getName())) {
                    collectionCatalogDefinition.remove(catalogDefinition);
                }
            }
            if(collectionCatalogDefinition.size() == 0)
                collectionCatalogDefinitions.remove(collectionCatalogDefinition);
        }
        if(location != -1) {
            this.ll_questionHolder.removeView(questionViews.get(location));
            questionViews.remove(location);
        }
    }

    public int indexOfQuestion(String name) {
        for (int i = 0; i < questionViews.size(); i++) {
            if(name.equals(questionViews.get(i).getCatalogDefinition().getName())) {
                return i;
            }
        }
        return -1;
    }



    private static abstract class BaseQuestion extends LinearLayout {
        protected Activity activity;
        private TextView tv_question;
        private EntryParsers.CatalogDefinition catalogDefinition;
        protected boolean hasValueChanged = false;

        public BaseQuestion(Activity activity, EntryParsers.CatalogDefinition catalogDefinition) {
            super(activity);
            this.activity = activity;
            this.catalogDefinition = catalogDefinition;
            this.setOrientation(VERTICAL);

            tv_question = new TextView(activity);
            tv_question.setGravity(Gravity.CENTER);
            this.addView(tv_question);
            tv_question.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv_question.setVisibility(GONE);


            if (catalogDefinition.getCatalog().equals("symptoms") || catalogDefinition.getCatalog().equals("conditions"))
                this.setQuestionTitle(catalogDefinition.getName());
        }

        public EntryParsers.Response getResponse() {
            return catalogDefinition.getResponse();
        }

        protected void setValue(Object value) {
            catalogDefinition.getResponse().setValue(value);
        }
        public Object getValue() {
            if(catalogDefinition.getResponse() != null)
                return catalogDefinition.getResponse().getValue();
            return null;
        }
        protected void setQuestionTitle(Spanned title){
            tv_question.setText(title);
            tv_question.setVisibility(VISIBLE);
        }
        protected void setQuestionTitle(String title) {
            tv_question.setText(title);
            tv_question.setVisibility(VISIBLE);
        }

        public boolean hasValueChanged(){
            return hasValueChanged;
        }

        public EntryParsers.CatalogDefinition getCatalogDefinition() {
            return catalogDefinition;
        }
    }

    private static class NumberQuestion extends BaseQuestion { // TODO update response object on value change.
        EditText editText;
        boolean integersOnly = false;

        public NumberQuestion(Activity activity, EntryParsers.CatalogDefinition catalogDefinition) {
            super(activity, catalogDefinition);
            if (catalogDefinition.getInputs().size() > 0) {
                editText = new EditText(getContext());

                double step = catalogDefinition.getInputs().get(0).getStep();
                if (step == Math.round(step)) { // Only excepts whole numbers.
                    integersOnly = true;
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else { // Decimals are aloud.
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
                editText.setGravity(Gravity.CENTER);
                editText.setSelectAllOnFocus(true);
                try {
                    setValue((double) catalogDefinition.getInputs().get(0).getValue());
                } catch (ClassCastException e) {
                    setValue(0.0);
                }

                if (catalogDefinition.getResponse() != null) {
                    try {
                        if (catalogDefinition.getResponse().getValue() instanceof Integer)
                            setValue(Integer.valueOf((Integer) catalogDefinition.getResponse().getValue()).doubleValue());
                        else
                            setValue((double) catalogDefinition.getResponse().getValue());
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
                this.addView(editText);


            }

        }

        public void setValue(double value) {
            if (integersOnly) {
                editText.setText(String.valueOf((int) value));
            } else {
                editText.setText(String.valueOf(value));
            }
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    hasValueChanged = true;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        @Override
        public Object getValue() {
            try {
                String stringValue = editText.getText().toString();
                Object objValue;
                if (integersOnly) {
                    objValue = Integer.valueOf(stringValue);
                } else {
                    objValue = Double.valueOf(stringValue);
                }

                if (getCatalogDefinition().getResponse() == null) {
                    getCatalogDefinition().setResponse(EntryParsers.createResponse(getCatalogDefinition(), objValue));
                } else {
                    getCatalogDefinition().getResponse().setValue(objValue);
                }
            } catch (NumberFormatException e) {
            }
            return super.getValue();
        }
    }

    private class CheckBoxQuestion extends BaseQuestion {
        Button bt_checkbox;

        public CheckBoxQuestion(Activity activity, final EntryParsers.CatalogDefinition catalogDefinition) {
            super(activity, catalogDefinition);
            bt_checkbox = new Button(new ContextThemeWrapper(activity, R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button);
            bt_checkbox.setText(Locales.read(activity, "catalogs." + catalogDefinition.getCatalog() + "." + catalogDefinition.getName()).resultIfUnsuccessful(catalogDefinition.getName()).createAT());
            bt_checkbox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt_checkbox.setSelected(!bt_checkbox.isSelected());
                    Integer value = (bt_checkbox.isSelected()) ? 1 : 0;
                    if (catalogDefinition.getResponse() == null) { // Create a new response object.
                        catalogDefinition.setResponse(EntryParsers.createResponse(catalogDefinition, value));
                    } else {
                        catalogDefinition.getResponse().setValue(value);
                    }
                    hasValueChanged = true;
                    triggerOnUpdateListener(catalogDefinition);
                }
            });
            this.addView(bt_checkbox);
            int margins = (int) Styling.getInDP(activity, 5);
            ((ViewGroup.MarginLayoutParams) bt_checkbox.getLayoutParams()).setMargins(margins, margins, margins, margins);

            if(catalogDefinition.getResponse() != null) {
                try {
                    if(catalogDefinition.getResponse().getValue() instanceof Integer)
                        bt_checkbox.setSelected(((int) catalogDefinition.getResponse().getValue()) == 1);
                    else
                        bt_checkbox.setSelected(((double) catalogDefinition.getResponse().getValue()) == 1.0);
                } catch (ClassCastException e) {}
            }
        }

    }
    private class SelectQuestion extends BaseQuestion {
        CheckinSelectorView checkinSelectorView;
        public SelectQuestion(Activity activity, final EntryParsers.CatalogDefinition catalogDefinition) {
            super(activity, catalogDefinition);
            checkinSelectorView = new CheckinSelectorView(activity);
            checkinSelectorView.setInputs(catalogDefinition.getInputs());
            this.addView(checkinSelectorView);


            if(catalogDefinition.getResponse() != null) {
                checkinSelectorView.setValue(catalogDefinition.getResponse().getValue());
            }

            checkinSelectorView.setOnValueChangeListener(new CheckinSelectorView.OnValueChangeListener() {
                @Override
                public void onValueChange(Object value) {
                    if(catalogDefinition.getResponse() == null) {
                        catalogDefinition.setResponse(EntryParsers.createResponse(catalogDefinition, value));
                    } else {
                        catalogDefinition.getResponse().setValue(value);
                    }
                    hasValueChanged = true;
                    triggerOnUpdateListener(catalogDefinition);
                }
            });
        }
    }
}