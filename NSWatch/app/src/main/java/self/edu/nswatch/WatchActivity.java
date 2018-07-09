package self.edu.nswatch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class WatchActivity extends AppCompatActivity {

    private Timer mainTimer;					//タイマー用
    private MainTimerTask mainTimerTask;		//タイマタスククラス
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watch);
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

        //タイマーのセット（一秒）-----------------------------------
        //タイマーインスタンス生成
        this.mainTimer = new Timer();
        //タスククラスインスタンス生成
        this.mainTimerTask = new MainTimerTask();
        //タイマースケジュール設定＆開始
        this.mainTimer.schedule(mainTimerTask, 0, 1000);

        // コメント配列生成
        String[] comment = {
                "この調子でがんばりましょう！",
                "3日、3週間、3年をめどにがんばろう！",
                "応援していますよ！この調子！",
                "結構、節約できましたね。",
                "もっともっと長生きしてくださいね。",
                "あなたの体はあなたが守ってあげなきゃね！",
                "１本吸うだけで崩れます。気をつけて！",
                "ここで吸っては、すべて水の泡ですよ。",
                "１本だけ。が命取り。気をつけてくださいね！",
                "順調ですね！がんばってください！",
                "食事が美味しいと思いませんか？",
                "肩の力を抜いて続けていきましょう！",
                "禁煙は美容にもいいですよ。",
                "禁煙は、健康によくてお財布にもやさしい！",
                "これで歯がヤニで汚れませんね！",
                "このがんばりを周りの人に自慢しましょう！",
                "この頑張りは、すばらしいですね！",
                "禁煙できる人って素敵ですよね。",
                "すばらしい自制心ですね！がんばって！"
        };

        //乱数を生成してランダムでコメント表示
        Random r = new Random();
        int n = r.nextInt(comment.length);
        //テキストをテキストビューに表示
        TextView commentLabel = (TextView) findViewById(R.id.commentLabel);
        commentLabel.setText(comment[n]);
    }

    //タイマークラス派生クラス------------------------------------------------------
    //run()に定周期で処理したい内容を記述
    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {

            //ここに定周期で実行したい処理を記述します
            Log.v("Log", "1秒タイマー実行");

            mHandler.post( new Runnable() {
                public void run() {
                   //ウォッチ表示メソッド実行
                    watchDateShow();
                }
            });

        }
    }

    //時計表示メソッド--------------------------------------------------------
    public void watchDateShow(){
    //public void watchDateShow() throws ParseException {
    //----------------------------------------------------------------------

        //保存されてるデータを読み込んで表示切り替え-----------------------------
        //Preferencesデータ読み込み
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        int nsFlag = data.getInt("nsFlag", 1);
        int smokeNum = data.getInt("smokeNum", 1);
        int smokePrice = data.getInt("smokePrice", 1);
        String startDateStr = data.getString("startDate", "");

        Log.v("Log", "nsFlag" + nsFlag);
        Log.v("Log", "smokeNum" + smokeNum);
        Log.v("Log", "smokePrice" + smokePrice);
        Log.v("Log", "startDate" + startDateStr); //yyyy/MM/dd HH:mm:ss

        //禁煙時間の表示------------------------------------------------------

        //禁煙スタート日時(startDate)を取得
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date startDate = new Date();
        try {
            startDate = sdf.parse(startDateStr);
        } catch (ParseException e) {

        }
        //現在日付を取得
        Date now = new Date();

        //差を求める(秒)
        // 日付をlong値に変換します。
        long nowLong = now.getTime();
        long startDateLong = startDate.getTime();
        // 差分の秒数を算出します。
        long since = (nowLong - startDateLong) / (1000);
        Log.v("Log", "差分の秒数" + since);

        //日時分秒を計算
        long i = since;
        long stop_sec = i % 60; //秒
        long stop_min = (i / 60) % 60;  //分
        long stop_hour = (i / 60 / 60) % 24;   //時
        long stop_day = (i / 60 / 60 / 24);   //日

        //一桁の数字を頭に0をつけて桁数を整える
        String stop_sec_str = String.format("%1$02d", stop_sec);
        String stop_min_str = String.format("%1$02d", stop_min);
        String stop_hour_str = String.format("%1$02d", stop_hour);
        //String stop_day_str = String.format("%1$02d", stop_day);

        //文字列を結合
        String str1 = (stop_day + "日" + stop_hour_str + ":" + stop_min_str + ":" + stop_sec_str);
        //テキストをテキストビューに表示
        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(str1);


        //吸わなかった本数の表示------------------------------------------------------

        //1日の喫煙数取得

        // 1本あたりの秒数
        long one_sec = (24*60*60)/smokeNum;
        // 吸わなかった本数
        long stop_num = since/one_sec;
        // 数値を3桁カンマ切りの文字列に整形する
        String str2 = String.format("%1$,3d", stop_num).replace(" ", "");
        //本を追加
        String str3 = (str2 + "本");
        //表示
        TextView numLabel = (TextView) findViewById(R.id.numLabel);
        numLabel.setText(str3);


        //節約できた金額の表示------------------------------------------------------

        //1箱の値段取得

        // 1本あたりの金額
        float one_price = smokePrice/20;
        // 節約できた金額
        float total_price = one_price * stop_num;
        long total_price_long = (long)total_price;
        // 数値を3桁カンマ切りの文字列に整形する
        String str4 = String.format("%1$,3d", total_price_long).replace(" ", "");
        //円を追加
        String str5 = (str4 + "円");
        //表示
        TextView priceLabel = (TextView) findViewById(R.id.priceLabel);
        priceLabel.setText(str5);


        //伸びた寿命(1本あたり5分30秒(330秒)伸びる計算)の表示--------------------------

        long life_total_sec = stop_num * 330;

        //int life_sec = life_total_sec%60; //秒
        long life_min = (life_total_sec/60)%60;  //分
        long life_hour = (life_total_sec/60/60)%24;   //時
        long life_day = (life_total_sec/60/60/24);   //日

        String longer_life = (life_day + "日" + life_hour + "時間" + life_min + "分");
        //表示
        TextView lifeLabel = (TextView) findViewById(R.id.lifeLabel);
        lifeLabel.setText(longer_life);

    }

    //リセットボタンメソッド--------------------------------------------------------
    public void resetBtn(View view) {
    //---------------------------------------------------------------------------

        //Preferencesに、"nsFlag"というkeyでデータ保存
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor e = pref.edit();
        e.putInt("nsFlag", 0);
        e.commit();

        //セグエ
        startActivity(new Intent(WatchActivity.this, StartActivity.class));
        finish();

    }

    //Twitterボタンメソッド--------------------------------------------------------
    public void tweet(View view) {
    //---------------------------------------------------------------------------
        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        TextView numLabel = (TextView) findViewById(R.id.numLabel);
        TextView priceLabel = (TextView) findViewById(R.id.priceLabel);
        TextView lifeLabel = (TextView) findViewById(R.id.lifeLabel);
        String dateText = dateLabel.getText().toString();
        String numText = numLabel.getText().toString();
        String priceText = priceLabel.getText().toString();
        String lifeText = lifeLabel.getText().toString();

        String strMessage = "禁煙継続中！\n･禁煙時間：" + dateText + "\n･禁煙できた本数：" + numText + "\n･節約できた金額：" + priceText + "\n･延びた寿命：" + lifeText + "\n";
        String strHashTag = "#禁煙ウォッチ" + "\n";
        String strUrl = "http://nonsmo.jp";
        String strTweet = "";
        try {
            strTweet = "http://twitter.com/intent/tweet?text=" + URLEncoder.encode(strMessage, "UTF-8") + "+" + URLEncoder.encode(strHashTag, "UTF-8")
                    + "&url=" + URLEncoder.encode(strUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strTweet));
        startActivity(intent);
    }
}
