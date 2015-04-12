package fathomlabs.flaredown;

/**
 * Created by Nik on 12/28/2014.
 */
public class Input {
    //{value: 0, label: "very_well", meta_label: "happy_face", helper: nil}

    private int _value;
    private String _label;
    private String _meta_label;
    private String _helper;

    public int get_value(){
        return _value;
    }

    public String get_label(){
        return _label;
    }

    public String get_meta_label(){
        return _meta_label;
    }

    public String get_helper() {return _helper;}

    public Input(int value, String label, String meta_label, String helper){
        _value = value;
        _label = label;
        _meta_label = meta_label;
        _helper = helper;
    }
}
