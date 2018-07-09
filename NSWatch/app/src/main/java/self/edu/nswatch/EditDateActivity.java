package self.edu.nswatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditDateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_date);
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

        //保存されてるデータを読み込んで表示切り替え-----------------------------
        //Preferencesデータ読み込み
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        String startDateStr = data.getString("startDate", "");

        //禁煙スタート日時(startDate)を取得
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date startDate = new Date();
        try {
            startDate = sdf.parse(startDateStr);
        } catch (ParseException e) {

        }
        //Calendarクラスのインスタンスを生成
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //DatePickerに設定
        DatePicker datepicker = (DatePicker) findViewById(R.id.datePicker);
        datepicker.updateDate(year, month, day);

        //最大値と最小値の設定-------------------------------------------------
        //Calendarクラスのインスタンスを生成
        Calendar cal2 = Calendar.getInstance();
        Date now = new Date();
        cal2.setTime(now);
        int yearMax = cal2.get(Calendar.YEAR);
        int monthMax = cal2.get(Calendar.MONTH);
        int dayMax = cal2.get(Calendar.DAY_OF_MONTH);

        // 最大値
        // 月は0が1月となる。
        GregorianCalendar maxDate = new GregorianCalendar();
        maxDate.set(yearMax, monthMax, dayMax);
        // 最小値
        GregorianCalendar minDate = new GregorianCalendar();
        minDate.set(1976, 0, 1);
        // セット
        datepicker.setMaxDate(maxDate.getTimeInMillis());
        datepicker.setMinDate(minDate.getTimeInMillis());

    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        //保存されてるデータを読み込んで表示切り替え-----------------------------
        //Preferencesデータ読み込み
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        int saveFlag = pref.getInt("saveFlag", 1);

        if (saveFlag == 1) {

            SharedPreferences.Editor e = pref.edit();
            e.putInt("saveFlag", 0);
            e.commit();
            // アクティビティを終了させる事により、一つ前のアクティビティへ戻る事が出来る。
            finish();
        }
    }

    public void nextBtn(View view) {

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        int year = datePicker.getYear();//年を取得
        int month = datePicker.getMonth();//月を取得
        int day = datePicker.getDayOfMonth();//日を取得
        String datePickerStr = (String.format("%d/%d/%d", year, month + 1, day));

        Intent i = new Intent(getApplicationContext(), EditTimeActivity.class);
        i.putExtra("datePickerStr", datePickerStr);
        startActivity(i);

        //セグエ
        //startActivity(new Intent(EditDateActivity.this, EditTimeActivity.class));
    }

}
