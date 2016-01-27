package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.Locales;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Styling;

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
    private static final String SI_CATALOG_NAME = "catalog name";

    private View fragmentRoot = null;
    private LinearLayout ll_questionHolder;
    private TextView tv_catalogName;
    private TextView tv_sectionTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        api = new API(getActivity());
        if (fragmentRoot == null){
            assignViews(inflater, container);
            init();
        }
        return fragmentRoot;
    }

    private void assignViews(LayoutInflater inflater, ViewGroup container) {
        fragmentRoot = inflater.inflate(R.layout.fragment_checkin_catalog_q, container, false);
        ll_questionHolder = (LinearLayout) fragmentRoot.findViewById(R.id.ll_questionHolder);
        tv_catalogName = (TextView) fragmentRoot.findViewById(R.id.tv_catalog);
        tv_sectionTitle = (TextView) fragmentRoot.findViewById(R.id.tv_question);
    }

    private void init() {
        if(catalogDefinitionLists != null) {
            //Set the catalog title.
            if(catalogDefinitionLists.size() > 0 && catalogDefinitionLists.get(0).size() > 0) {
                switch (catalogDefinitionLists.get(0).get(0).getCatalog()) {
                    case "conditions":
                    case "symptoms":
                        tv_catalogName.setText(Locales.read(getActivity(), "onboarding.edit_" + catalogDefinitionLists.get(0).get(0).getCatalog()).capitalize1Char().createAT());
                        break;
                    default:
                        tv_catalogName.setText(Locales.read(getActivity(), "catalogs." + catalogDefinitionLists.get(0).get(0).getCatalog() + ".catalog_description").capitalize1Char().createAT());
                        break;
                }

                //Set the section title.
                String sectionTitle = "--";
                switch (catalogDefinitionLists.get(0).get(0).getCatalog()) {
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
                        sectionTitle = Locales.read(getActivity(), "catalogs." + catalogDefinitionLists.get(0).get(0).getCatalog() + ".section_" + section + "_prompt").resultIfUnsuccessful(sectionTitle).create();
                        break;
                }
                //sectionTitle = String.valueOf(section);
                tv_sectionTitle.setText(sectionTitle);
            }
            for (List<EntryParsers.CatalogDefinition> catalogDefinitions : catalogDefinitionLists) {
                for (EntryParsers.CatalogDefinition catalogDefinition : catalogDefinitions) {
                    appendQuesiton(catalogDefinition);
                }
            }
        }
    }



    private List<BaseQuestion> questionViews = new ArrayList<>();

    public void setQuestions(List<List<EntryParsers.CatalogDefinition>> catalogDefinitions, Integer section) {
        //Toast.makeText(getActivity(), "CheckinCatalogQFragment:setQuestions()",Toast.LENGTH_SHORT).show();
        this.catalogDefinitionLists = catalogDefinitions;
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
        }
        if(questionView != null) {
            ll_questionHolder.addView(questionView);
            questionViews.add(questionView);
        }
    }

    public void removeQuestion(String name) {
        int location = indexOfQuestion(name);
        this.ll_questionHolder.removeView(questionViews.get(location));
        questionViews.remove(location);
    }

    public int indexOfQuestion(String name) {
        for (int i = 0; i < questionViews.size(); i++) {
            if(questionViews.get(i).getCatalogDefinition().getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }



    private static abstract class BaseQuestion extends LinearLayout {
        protected Activity activity;
        private TextView tv_question;
        private EntryParsers.CatalogDefinition catalogDefinition;

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
            return catalogDefinition.getResponse().getValue();
        }
        protected void setQuestionTitle(Spanned title){
            tv_question.setText(title);
            tv_question.setVisibility(VISIBLE);
        }
        protected void setQuestionTitle(String title) {
            tv_question.setText(title);
            tv_question.setVisibility(VISIBLE);
        }

        public EntryParsers.CatalogDefinition getCatalogDefinition() {
            return catalogDefinition;
        }
    }

    private static class CheckBoxQuestion extends BaseQuestion {
        Button bt_checkbox;
        public CheckBoxQuestion(Activity activity, EntryParsers.CatalogDefinition catalogDefinition) {
            super(activity, catalogDefinition);
            bt_checkbox = new Button(new ContextThemeWrapper(activity, R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button);
            bt_checkbox.setText(Locales.read(activity, "catalogs." + catalogDefinition.getCatalog() + "." + catalogDefinition.getName()).resultIfUnsuccessful(catalogDefinition.getName()).createAT());
            bt_checkbox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt_checkbox.setSelected(!bt_checkbox.isSelected());
                }
            });
            this.addView(bt_checkbox);
            int margins = (int) Styling.getInDP(activity, 5);
            ((ViewGroup.MarginLayoutParams) bt_checkbox.getLayoutParams()).setMargins(margins, margins, margins, margins);
        }
    }
    private static class SelectQuestion extends BaseQuestion {
        CheckinSelectorView checkinSelectorView;
        public SelectQuestion(Activity activity, EntryParsers.CatalogDefinition catalogDefinition) {
            super(activity, catalogDefinition);
            checkinSelectorView = new CheckinSelectorView(activity);
            checkinSelectorView.setInputs(catalogDefinition.getInputs());
            this.addView(checkinSelectorView);
        }
    }
}