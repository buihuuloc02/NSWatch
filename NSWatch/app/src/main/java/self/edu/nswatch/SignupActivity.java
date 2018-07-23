package self.edu.nswatch;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import self.edu.nswatch.dummy.BaseActivity;
//import android.widget.TextView;

public class SignupActivity extends BaseActivity {

    private EditText editTextPrice;
    private EditText editTextNum ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        initView();
    }


    private void initView() {
        editTextPrice = (EditText) this.findViewById(R.id.editTextPrice);
        editTextNum = (EditText) this.findViewById(R.id.editTextNum);

        if (isLanguageJA()) {
            editTextPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else {
            String manufacturer = Build.MANUFACTURER;
            String manufacturer_error = "samsung";
            editTextPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editTextPrice.setRawInputType(Configuration.KEYBOARD_QWERTY);
            editTextPrice.setFilters(new InputFilter[]{new InputFilterMinMax("0", "1000"), new InputFilter.LengthFilter(6)});
            editTextPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String value = s.toString();
                    if (!TextUtils.isEmpty(value)) {
                        float valueFloat = Float.parseFloat(value);
                        if (valueFloat >= 1000) {
                            editTextPrice.setText("");
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        editTextPrice.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        editTextNum.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
    }


    public void saveBtn(View view) {

        //本数の入力フォームの入力テキストをintデータにする
        Editable getText = editTextNum.getText();
        //値段の入力フォームの入力テキストをintデータにする

        Editable getText2 = editTextPrice.getText();

        if ((editTextNum.getText().toString().equals("")) || ((editTextPrice.getText().toString().equals("")))) {

            Log.v("Log", "未入力");
            //アラート表示
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            //ダイアログタイトルをセット
            alert.setTitle(getString(R.string.text_title_error));
            //ダイアログメッセージをセット
            alert.setMessage(getString(R.string.text_input_number_correct));
            // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alert.setPositiveButton("OK", null);
            //ダイアログ表示
            alert.show();

        } else {

            //intに変換
            int smokeNum = Integer.parseInt(getText.toString());
            float smokePrice_en = Float.parseFloat(getText2.toString());
            int smokePrice = (int) smokePrice_en;


            if ((smokeNum == 0) || (smokePrice == 0)) {

                Log.v("Log", "数値が0");
                //アラート表示
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                //ダイアログタイトルをセット
                alert.setTitle(getString(R.string.text_title_error));
                //ダイアログメッセージをセット
                alert.setMessage(getString(R.string.text_input_number_correct));
                // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alert.setPositiveButton("OK", null);
                //ダイアログ表示
                alert.show();

            } else {
                //現在時間取得
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String startDateStr = sdf.format(new Date());//#=> 2011/06/15/ 04:49:01

                //Preferencesに、データ保存
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                e.putInt("nsFlag", 1);
                e.putInt("smokeNum", smokeNum);
                e.putInt("smokePrice", smokePrice);
                e.putFloat("smokePrice_en", smokePrice_en);
                e.putString("startDate", startDateStr);
                //e.putString("startDate", now.getTime());
                e.commit();

                // アクティビティを終了させる事により、一つ前のアクティビティへ戻る事が出来る。
                finish();
            }

        }
    }

}
