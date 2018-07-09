package self.edu.nswatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;

public class StartActivity extends FragmentActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        StartViewPagerAdapter startViewPagerAdapter = new StartViewPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(startViewPagerAdapter);

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

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        //保存されてるデータを読み込んで表示切り替え-----------------------------
        //Preferencesデータ読み込み
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        int smokeNum = data.getInt("smokeNum", 0);

        if (smokeNum != 0) {
            //セグエ
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    //スタートボタン
    public void startBtn(View view) {
        //セグエ
        startActivity(new Intent(StartActivity.this, SignupActivity.class));
    }
}
