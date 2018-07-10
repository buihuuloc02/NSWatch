package self.edu.nswatch.dummy;

import android.support.v4.app.Fragment;

import java.util.Locale;

/**
 * Created by Administrator on 7/10/2018.
 */

public class BaseFragment extends Fragment {
    private String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public boolean isLanguageJA() {
        String langJa = "ja";
        if (getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
            return true;
        }
        return false;
    }
}
