package self.edu.nswatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import self.edu.nswatch.dummy.BaseFragment;

public class StartViewActivity2 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start_view2, null);
        View img = (View) view.findViewById(R.id.imgStart2);
        if (isLanguageJA()) {
            img.setBackgroundResource(R.drawable.start2_ja);
        }else{
            img.setBackgroundResource(R.drawable.start2_en);
        }
        return view;
    }
}
