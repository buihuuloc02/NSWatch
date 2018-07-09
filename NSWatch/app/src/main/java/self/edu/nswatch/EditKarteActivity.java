package self.edu.nswatch;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditKarteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_karte);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //保存されてるデータを読み込んで表示初期設定-----------------------------
        //Preferencesデータ読み込み
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        int smokeNum = data.getInt("smokeNum", 1);
        int smokePrice = data.getInt("smokePrice", 1);
        String smokeNumStr = String.valueOf(smokeNum);
        String smokePriceStr = String.valueOf(smokePrice);

        //入力フォームに初期設定として数値入力する
        EditText editTextNum = (EditText)this.findViewById(R.id.editTextNum);
        editTextNum.setText (smokeNumStr);
        EditText editTextPrice = (EditText)this.findViewById(R.id.editTextPrice);
        editTextPrice.setText (smokePriceStr);

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
    }

    public void saveBtn(View view) {

        //本数の入力フォームの入力テキストをintデータにする
        EditText editTextNum = (EditText)this.findViewById(R.id.editTextNum);
        Editable getText = editTextNum.getText();
        //値段の入力フォームの入力テキストをintデータにする
        EditText editTextPrice = (EditText)this.findViewById(R.id.editTextPrice);
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

        }else{

            //intに変換
            int smokeNum = Integer.parseInt(getText.toString());
            int smokePrice = Integer.parseInt(getText2.toString());

            if ((smokeNum == 0)||(smokePrice == 0)){

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

            }else {
                //現在時間取得
                //Date now = new Date();
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                //String startDateStr = sdf.format(new Date());//#=> 2011/06/15/ 04:49:01

                //Preferencesに、データ保存
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                //e.putInt("nsFlag", 1);
                e.putInt("smokeNum", smokeNum);
                e.putInt("smokePrice", smokePrice);
                //e.putString("startDate", startDateStr);
                //e.putString("startDate", now.getTime());
                e.commit();

                // アクティビティを終了させる事により、一つ前のアクティビティへ戻る事が出来る。
                finish();
            }

        }
    }

}
