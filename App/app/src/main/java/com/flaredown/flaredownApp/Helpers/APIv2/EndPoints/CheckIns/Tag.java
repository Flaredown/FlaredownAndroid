package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to represent a tag.
 */
public class Tag implements Serializable{
    private int id;
    private transient MetaTrackable metaTrackable;

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

    private final static String MT_ID = "mt_id";
    private final static String MT_NAME = "mt_name";
    private final static String MT_TYPE = "mt_type";
    private final static String MT_CREATED_AT = "mt_createdAt";
    private final static String MT_UPDATED_AT = "mt_updatedAt";
    private final static String MT_CACHED_AT = "mt_cachedAt";

    // Overriding java's serialization, this is because the realm database does not allow serialisation
    // and a MetaTrackable is a serializable object
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // Default serialzation.
        oos.defaultWriteObject();

        Map<String, Object> data = new HashMap<>();
        data.put(MT_ID, metaTrackable.getId());
        data.put(MT_NAME, metaTrackable.getName());
        data.put(MT_TYPE, metaTrackable.getTypeRaw());
        data.put(MT_CREATED_AT, metaTrackable.getCreatedAtRaw());
        data.put(MT_UPDATED_AT, metaTrackable.getUpdatedAtRaw());
        data.put(MT_CACHED_AT, metaTrackable.getCachedAtRaw());
        oos.writeObject(data);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        // Default deserializarion.
        ois.defaultReadObject();

        Map<String, Object> data = (HashMap<String, Object>) ois.readObject();
        if(data.containsKey(MT_ID)) {
            if(metaTrackable == null) metaTrackable = new MetaTrackable();
            metaTrackable.setId((Integer) data.get(MT_ID));
            metaTrackable.setName((String) data.get(MT_NAME));
            metaTrackable.setTypeRaw((String) data.get(MT_TYPE));
            metaTrackable.setCreatedAtRaw((Long) data.get(MT_CREATED_AT));
            metaTrackable.setUpdatedAtRaw((Long) data.get(MT_UPDATED_AT));
            metaTrackable.setCachedAtRaw((Long) data.get(MT_CACHED_AT));
        }
    }
}
