package self.edu.nswatch.dummy;

import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by Administrator on 7/11/2018.
 */

public class BaseActivity extends AppCompatActivity {
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

    public class InputFilterMinMax implements InputFilter {

        private float min, max;

        public InputFilterMinMax(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Float.parseFloat(min);
            this.max = Float.parseFloat(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                float input = Float.parseFloat(dest.toString() + source.toString());
                if (isInRange(min, max, input) && !checkHasTwoDigit(input + ""))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(float a, float b, float c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public boolean checkHasTwoDigit(String value) {
        if (!TextUtils.isEmpty(value) && value.contains(".")) {
            String[] a = value.split("\\.");
            if (a.length >= 2) {
                int decimals = a[1].length();
                if (decimals > 2) {
                    return true;
                }
            }
        }
        return false;
    }
}
