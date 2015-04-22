package fathomlabs.flaredown;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Nik on 2/16/2015.
 */
public class BundleConverter {
    public static ArrayList<Input> ConvertBundleToInputs(Bundle bundles){
        ArrayList<Input> inputs = new ArrayList<Input>();
        int i = 0;
        for(String bundleLabel : bundles.getBundle("labels").keySet()){

            Bundle bundle = bundles.getBundle("inputs").getBundle(bundles.getBundle("labels").getString(bundleLabel));
            int value = bundle.getInt("value");
            String label = bundle.getString("label");
            String meta_label = bundle.getString("meta_label");
            String helper = bundle.getString("helper");

            inputs.add(new Input(value, label, meta_label, helper));
            i++;
        }
        return inputs;
    }

    public static Bundle ConvertInputsToBundle(ArrayList<Input> inputs){
        Bundle bundles = new Bundle();

        for(Input input : inputs){
            Bundle bundle = new Bundle();
            bundle.putInt("value", input.get_value());
            bundle.putString("label", input.get_label());
            bundle.putString("meta_label", input.get_meta_label());
            bundle.putString("helper", input.get_helper());
            bundles.putBundle(input.get_label(), bundle);
        }
        return bundles;
    }

    public static Bundle ConvertInputsToLabelsBundle(ArrayList<Input> inputs){
        Bundle labels = new Bundle();
        int i = 0;
        for(Input input : inputs){

            labels.putString("label" + i, input.get_label());
            i++;
        }

        return labels;
    }
}
