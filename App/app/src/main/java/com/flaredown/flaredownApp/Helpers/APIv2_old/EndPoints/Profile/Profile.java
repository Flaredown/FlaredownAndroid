package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Profile;


import com.flaredown.flaredownApp.Helpers.APIv2_old.Helper.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Profile {
    private Calendar birth_date;
    private String country_id;
    private Calendar created_at;
    private String day_habit_id;
    private int day_walking_hours;
    private String education_level_id;
    private List<String> ethnicity_ids;
    private int id;
    private String onboarding_step_id;
    private String sex_id;
    private Calendar updated_at;

    public Profile(JSONObject json) throws JSONException{
        JSONObject profileJson = json.getJSONObject("profile");
        this.birth_date = Date.stringToCalendar(profileJson.getString("birth_date"));
        this.country_id = profileJson.getString("country_id");
        this.created_at = Date.stringToCalendar(profileJson.getString("created_at"));
        this.day_habit_id = profileJson.getString("day_habit_id");
        this.day_walking_hours = profileJson.getInt("day_walking_hours");
        this.education_level_id = profileJson.getString("education_level_id");
        ethnicity_ids = new ArrayList<>();
        JSONArray eth_ids = profileJson.getJSONArray("ethnicity_ids");
        for (int i =0; i < eth_ids.length(); i++){
            ethnicity_ids.add((String) eth_ids.get(i));
        }
        this.id = profileJson.getInt("id");
        this.onboarding_step_id = profileJson.getString("onboarding_step_id");
        this.sex_id = profileJson.getString("sex_id");
        this.updated_at = Date.stringToCalendar(profileJson.getString("updated_at"));
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject output = new JSONObject();
        output.put("birth_date",Date.calendarToString(birth_date));
        output.put("country_id",country_id);
        output.put("created_at",Date.calendarToString(created_at));
        output.put("day_habit_id",day_habit_id);
        output.put("day_walking_hours",day_walking_hours);
        output.put("education_level_ids",education_level_id);
        JSONArray eth = new JSONArray();
        for(String eth_value : ethnicity_ids){
            eth.put(eth_value);
        }
        output.put("ethnicity_ids",eth);
        output.put("id",id);
        output.put("onboarding_step_id",onboarding_step_id);
        output.put("sex_id",sex_id);
        output.put("updated_at",Date.calendarToString(updated_at));
        JSONObject profile = new JSONObject();
        profile.put("profile",output);

        return profile;
    }

    public Calendar getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Calendar birth_date) {
        this.birth_date = birth_date;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public Calendar getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Calendar created_at) {
        this.created_at = created_at;
    }

    public String getDay_habit_id() {
        return day_habit_id;
    }

    public void setDay_habit_id(String day_habit_id) {
        this.day_habit_id = day_habit_id;
    }

    public int getDay_walking_hours() {
        return day_walking_hours;
    }

    public void setDay_walking_hours(int day_walking_hours) {
        this.day_walking_hours = day_walking_hours;
    }

    public String getEducation_level_id() {
        return education_level_id;
    }

    public void setEducation_level_id(String education_level_id) {
        this.education_level_id = education_level_id;
    }

    public List<String> getEthnicity_ids() {
        return ethnicity_ids;
    }

    public void setEthnicity_ids(List<String> ethnicity_ids) {
        this.ethnicity_ids = ethnicity_ids;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOnboarding_step_id() {
        return onboarding_step_id;
    }

    public void setOnboarding_step_id(String onboarding_step_id) {
        this.onboarding_step_id = onboarding_step_id;
    }

    public String getSex_id() {
        return sex_id;
    }

    public void setSex_id(String sex_id) {
        this.sex_id = sex_id;
    }

    public Calendar getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Calendar updated_at) {
        this.updated_at = updated_at;
    }
}
