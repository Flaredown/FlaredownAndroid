package fathomlabs.flaredown;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nik on 12/28/2014.
 */
public class Select {
    /*[{
        // 0 Very Well
        // 1 Slightly below par
        // 2 Poor
        // 3 Very poor
        // 4 Terrible

        name: :general_wellbeing,
        kind: :select,
        inputs: [
            {value: 0, label: "very_well", meta_label: "happy_face", helper: nil},
            {value: 1, label: "slightly_below_par", meta_label: "neutral_face", helper: nil},
            {value: 2, label: "poor", meta_label: "frowny_face", helper: nil},
            {value: 3, label: "very_poor", meta_label: "sad_face", helper: nil},
            {value: 4, label: "terrible", meta_label: "sad_face", helper: nil},
        ]
    }];
    */
    private String _name;
    private List<Input> _inputs = new ArrayList<Input>();

    public String get_name() {
        return _name;
    }

    public String get_kind() {
        return "select";
    }

    public Bundle get_inputs() {
        Bundle bundle = new Bundle();
        for(Input input : _inputs) {
            Bundle inputBundle = new Bundle();
            inputBundle.putString("label", input.get_label());
            inputBundle.putString("meta_label", input.get_meta_label());
            inputBundle.putInt("value", input.get_value());
            bundle.putAll(inputBundle);
        }
        return bundle;
    }

    public Select(String name, List<Input> inputs){
        _name = name;
        _inputs = inputs;
    }


}
