package com.flaredown.flaredownApp.Activities.Checkin.InputViews;

public interface TreatmentChangeListener {
    void onRemove();
    void onUpdateDose(String dose);
    void onIsTakenUpdate(boolean isTaken);
}
