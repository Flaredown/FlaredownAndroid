package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

import io.intercom.com.google.gson.annotations.SerializedName;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Provides extra information about a trackable.
 */
public class MetaTrackable extends RealmObject {
    private Integer colorId;

    @PrimaryKey
    private String realmId;

    private Integer id; // Unique to the trackable, used to prevent duplicates.

    private String name;

    @Ignore
    private transient TrackableType type; // Work around for Realm not being able to store the Calendar object.

    @SerializedName("type")
    private String typeRaw;

    @Ignore
    private transient Calendar createdAt; // Work around for Realm not being able to store the Calendar object.

    @SerializedName("createdAt")
    private Long createdAtRaw;

    @Ignore
    private transient Calendar updatedAt; // Work around for Realm not being able to store the Calendar object.

    @SerializedName("updatedAt")
    private Long updatedAtRaw;

    @Ignore
    private transient Calendar cachedAt; // Work around for Realm not being able to store the Calendar object.

    @SerializedName("cachedAt")
    private Long cachedAtRaw;

    public MetaTrackable() {
        this.colorId = 1;
        this.id = 0;
        this.realmId = "----0";
    }

    public MetaTrackable(TrackableType trackableType) {
        setType(trackableType);
    }

    public MetaTrackable(JSONObject jObject) throws JSONException{
        this.colorId = jObject.optInt("color_id", 1);
        setId(jObject.getInt("id"));
        this.name = jObject.getString("name");
        setType(TrackableType.valueOfs(jObject.getString("type")));
        this.createdAtRaw = Date.stringToMillis(jObject.optString("created_at", null));
        this.updatedAtRaw = Date.stringToMillis(jObject.optString("updated_at", null));
    }

    public Integer getColorId() {
        return colorId;
    }

    public void setColorId(Integer colorId) {
        this.colorId = colorId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        getRealmId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrackableType getType() {
        try {
            return TrackableType.valueOf(getTypeRaw());
        } catch(Exception e) {
            return null;
        }
    }

    public void setType(TrackableType type) {
        if(type != null)
            setTypeRaw(type.name());
    }

    public String getTypeRaw() {
        return typeRaw;
    }

    public void setTypeRaw(String type) {
        typeRaw = type;
        getRealmId();
    }

    public Calendar getCreatedAt() {
        return Date.millisToCalendar(getCreatedAtRaw());
    }

    public void setCreatedAt(Calendar createdAt) {
        setCreatedAtRaw(createdAt.getTimeInMillis());
    }

    public Long getCreatedAtRaw() {
        return createdAtRaw;
    }

    public void setCreatedAtRaw(Long createdAtRaw) {
        this.createdAtRaw = createdAtRaw;
    }

    public Calendar getUpdatedAt() {
        return Date.millisToCalendar(getUpdatedAtRaw());
    }

    public void setUpdatedAt(Calendar updatedAt) {
        setUpdatedAtRaw(updatedAt.getTimeInMillis());
    }

    public Long getUpdatedAtRaw() {
        return updatedAtRaw;
    }

    public void setUpdatedAtRaw(Long updatedAtRaw) {
        this.updatedAtRaw = updatedAtRaw;
    }

    /**
     * Get the time when object stored in DB for caching.
     * @return The time when the object was stored in the DB for caching.
     */
    public Calendar getCachedAt() {
        return Date.millisToCalendar(getCachedAtRaw());
    }

    /**
     * Set the time when the object is stored in the DB for caching.
     * @param cachedAt The time when the object
     */
    public void setCachedAt(Calendar cachedAt) {
        setCachedAtRaw(cachedAt.getTimeInMillis());
    }

    public Long getCachedAtRaw() {
        return cachedAtRaw;
    }

    public void setCachedAtRaw(Long cachedAtRaw) {
        this.cachedAtRaw = cachedAtRaw;
    }

    /**
     * Removes all elements cached in realm which have passed the maxAge given.
     * @param realmInstance The realmInstance to alter.
     * @param maxAge The max age of the elements.
     */
    public static void clearExpiredItems(Realm realmInstance, long maxAge) {
        realmInstance.beginTransaction();
        RealmQuery<MetaTrackable> query = realmInstance.where(MetaTrackable.class).lessThanOrEqualTo("cachedAtRaw", Calendar.getInstance().getTimeInMillis() - maxAge).isNotNull("cachedAtRaw");
        query.findAll().clear();
        realmInstance.commitTransaction();
    }

    public String getRealmId() {
        setRealmId(calculateRealmId(getType(), getId()));
        return realmId;
    }

    public static String calculateRealmId(TrackableType trackableType, Integer id) {
        return (trackableType == null ? "---" : trackableType.toString()) + "-" + (id == null ? "---" : id);
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }
}
