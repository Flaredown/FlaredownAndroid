package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints;

import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.MetaTrackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Used to represent a tag.
 */
public class Tag implements Serializable{
    private int id;
    private MetaTrackable metaTrackable;

    /**
     * Create a new tag object
     * @param id The id of the tag.
     */
    public Tag(Integer id) {
        this(id, null);
    }

    public Tag(MetaTrackable metaTrackable) {
        setMetaTrackable(metaTrackable);
        setId(metaTrackable.getId());
    }

    /**
     * Create a new tag object.
     * @param id The id of the tag.
     * @param metaTrackable The meta data for the tag (name etc).
     */
    public Tag(Integer id, MetaTrackable metaTrackable) {
        this.id = id;
        setMetaTrackable(metaTrackable);
    }

    /**
     * Create a new tag object from json object.
     * @param jObject Json object.
     */
    public Tag(JSONObject jObject) throws JSONException {
        this.metaTrackable = new MetaTrackable(jObject);
        this.id = metaTrackable.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MetaTrackable getMetaTrackable() {
        return metaTrackable;
    }

    public void setMetaTrackable(MetaTrackable metaTrackable) {
        if(metaTrackable != null && !TrackableType.TAG.equals(metaTrackable.getType())) {
            throw new IllegalStateException("Cannot set MetaTrackable which is not a Tag");
        }
        this.metaTrackable = metaTrackable;
    }

    @Override
    public int hashCode() {
//        final int HASH_MULTIPLIER = 31; // Normally a random prime number.
        final int HASH_MULTIPLIER = 0; // Just because there is only a single field which the hashcode is calculated from.
        int result = 0;
        result = HASH_MULTIPLIER * result + id;
//        result = HASH_MULTIPLIER * result + (name != null ? name.hashCode() : 0);
//        result = HASH_MULTIPLIER * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null || (this.getClass() != o.getClass())) return false;

        Tag other = (Tag) o;
        return id == other.id;// &&
//                (name != null && name.equals(other.name)) &&
//                (type != null && type.equals(other.type));
    }
}
