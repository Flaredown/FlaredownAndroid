package fathomlabs.flaredown;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Nik on 2/16/2015.
 */
public class SelectPage extends Fragment {
    private Integer _response=0;
    private ArrayList<Input> _inputs = new ArrayList<Input>();
    public int get_response(){
        return _response;
    }

    public static SelectPage newInstance(ArrayList<Input> inputs) {
        SelectPage fragment = new SelectPage();
        Bundle args = new Bundle();
        args.putBundle("inputs", BundleConverter.ConvertInputsToBundle(inputs));
        args.putBundle("labels", BundleConverter.ConvertInputsToLabelsBundle(inputs));
        fragment.setArguments(args);
        return fragment;
    }

    public SelectPage(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.select_page, container, false);

        ArrayList<Input> inputs = BundleConverter.ConvertBundleToInputs(getArguments());

        Select select = new Select("general_wellbeing", inputs);

        Point size = getWindowSize(inflater);
        int width = size.x;
        int height = size.y;

        Context context = inflater.getContext();

        TextView textView = new TextView(context);
        textView.setText(select.get_name());
        view.addView(textView);

        Integer i = 1;
        int yLocation = 50;
        LinearLayout inputLayout = generateLinearLayout(context, width, height, LinearLayout.HORIZONTAL);
        for(Input input : inputs){
            LinearLayout optionLayout = generateLinearLayout(context, width/5, height, LinearLayout.VERTICAL);

            ImageButton imageButton = new ImageButton(context);
            imageButton.setImageResource(getResources().getIdentifier(input.get_meta_label(), "drawable", context.getPackageName()));
            imageButton.setX(i * 25);
            imageButton.setY(yLocation);
            imageButton.setOnClickListener(happyListener);

            TextView text = new TextView(context);
            text.setText(i.toString());

            optionLayout.addView(imageButton);
            optionLayout.addView(text);

            inputLayout.addView(optionLayout);

            i++;
        }
        view.addView(inputLayout);
        return view;

    }

    private LinearLayout generateLinearLayout(Context context, int width, int height, int orientation) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        layout.setOrientation(orientation);
        return layout;
    }

    private Point getWindowSize(LayoutInflater inflater){
        WindowManager wm = (WindowManager) inflater.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private View.OnClickListener happyListener = new View.OnClickListener() {
        public void onClick(View view) {
            _response = 1;
            Toast toast = Toast.makeText(view.getContext(), _response.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    };

}
