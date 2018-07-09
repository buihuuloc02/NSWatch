package self.edu.nswatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_time);
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
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
        tp.setCurrentHour(hour);
        tp.setCurrentMinute(minute);

    }

    public void saveBtn(View view) {

        //Dateのフォーマット整えてStartDateに変換
        TimePicker tp = (TimePicker)findViewById(R.id.timePicker);
        int hour = tp.getCurrentHour();//時間
        int minute = tp.getCurrentMinute();//分
        Intent i = getIntent();
        String datePickerStr = i.getStringExtra("datePickerStr");
        String timePickerStr = (String.format("%d:%d:00",hour,minute ));
        String startDateStr = datePickerStr + " " + timePickerStr;

        //Preferencesに、データ保存
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor e = pref.edit();
        e.putInt("saveFlag", 1);
        e.putString("startDate", startDateStr);
        //e.putString("startDate", now.getTime());
        e.commit();

        // アクティビティを終了させる事により、一つ前のアクティビティへ戻る事が出来る。
        finish();
    }

}