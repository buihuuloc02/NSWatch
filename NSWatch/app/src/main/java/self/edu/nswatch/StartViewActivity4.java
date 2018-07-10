package self.edu.nswatch;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import self.edu.nswatch.dummy.BaseFragment;

public class StartViewActivity4 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start_view4, null);
        View img = (View) view.findViewById(R.id.imgStart4);
        if (isLanguageJA()) {
            img.setBackgroundResource(R.drawable.start4_ja);
        }else{
            img.setBackgroundResource(R.drawable.start4_en);
        }
        return view;
    }
}
