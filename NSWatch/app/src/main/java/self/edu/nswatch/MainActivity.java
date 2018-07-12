package self.edu.nswatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.nend.android.NendAdInterstitial;
import net.zucks.listener.AdInterstitialListener;
import net.zucks.view.AdInterstitial;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

//import java.util.Locale;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.FragmentActivity;
//import android.view.Menu;
//import android.widget.Toast;
//レビューダイアログ用

//import javax.net.ssl.SSLParameters;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Timer mainTimer;                    //タイマー用
    private MainTimerTask mainTimerTask;        //タイマタスククラス
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    private int nendFlag = 1;      //Nend用表示フラグ　1＝表示　0＝非表示
    private String interNetwork;   //NendかZucksか判別用string "nend" or "zucks"
    private AdInterstitial mAdInterstitial; //Zucks用

    //Firebase用
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase初期設定--------------------------------------------------------
        // アナリティクス設定
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        //int nsFlag = data.getInt("nsFlag", 0);
        //Log.v("tag", "nsFlag" + nsFlag);
        int smokeNum = data.getInt("smokeNum", 0);

        if (smokeNum == 0) {
            //セグエ
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            overridePendingTransition(0, 0);//アニメーションをOFF
            finish();

        } else {
            //セグエ
            //startActivity(new Intent(MainActivity.this, WatchActivity.class));
            //overridePendingTransition(0, 0);//アニメーションをOFF
            //finish();

            //--------------------------インタースティシャル広告ロード----------------------------//
            //表示切り替えメソッド
            sspLoad();   //Firebaseからssp読み込み

            //Nend用広告---------------------------------------------------------------------
            nendFlag = 1;
            //Nend用テストコード
            //NendAdInterstitial.loadAd(getApplicationContext(), "8c278673ac6f676dae60a1f56d16dad122e23516", 213206);
            //Nend用本番コード
            //489ddc20-e347-4173-9c1b-a856a3256e3a    82f2944b794306d1542c78f5114b540dbcbbf9da
            NendAdInterstitial.loadAd(getApplicationContext(), "82f2944b794306d1542c78f5114b540dbcbbf9da", 618858);

            //Nend用取得ステータス表示
            NendAdInterstitial.setListener(new NendAdInterstitial.OnCompletionListener() {
                @Override
                public void onCompletion(NendAdInterstitial.NendAdInterstitialStatusCode status) {
                    switch (status) {
                        case SUCCESS:
                            // 成功
                            Log.v("Log", "Nendロード:成功");
                            if (nendFlag == 1) {
                                //インタースティシャル表示メソッド実行
                                showInterstitialAd();
                            }
                            break;
                        case INVALID_RESPONSE_TYPE:
                            // 不明な広告タイプ
                            Log.v("Log", "Nendロード:不明な広告タイプ");
                            break;
                        case FAILED_AD_REQUEST:
                            // 広告取得失敗
                            //showInterstitialAd();
                            Log.v("Log", "Nendロード:広告取得失敗");
                            break;
                        case FAILED_AD_INCOMPLETE:
                            // 広告取得未完了
                            Log.v("Log", "Nendロード:広告取得未完了");
                            break;
                        case FAILED_AD_DOWNLOAD:
                            // 広告画像取得失敗
                            Log.v("Log", "Nendロード:広告画像取得失敗");
                            break;
                        default:
                            break;
                    }
                }
            });
            //Nend end----------------------------------------------------------

            //Zucks広告ロード----------------------------------------------------
            initZuckAd();
            // 広告情報のロード
            //mAdInterstitial.show();

            //Zucks広告ロードend----------------------------------------------------
            //-------------------------インタースティシャル広告ロードend----------------------------//

            //ナビゲーションバー表示
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menuNav = navigationView.getMenu();
            MenuItem googlePlayItem = menuNav.findItem(R.id.menuGooglePlay);
            String langJa = "ja";
            if (!getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
                if (googlePlayItem != null) {
                    googlePlayItem.setVisible(false);
                }
            }
            navigationView.setNavigationItemSelectedListener(this);

            //タイマーのセット（一秒）-----------------------------------
            //タイマーインスタンス生成
            this.mainTimer = new Timer();
            //タスククラスインスタンス生成
            this.mainTimerTask = new MainTimerTask();
            //タイマースケジュール設定＆開始
            this.mainTimer.schedule(mainTimerTask, 0, 1000);

            // コメント配列生成
            String[] comment = getResources().getStringArray(R.array.array_comment);

            //乱数を生成してランダムでコメント表示
            Random r = new Random();
            int n = r.nextInt(comment.length);
            //テキストをテキストビューに表示
            TextView commentLabel = (TextView) findViewById(R.id.commentLabel);
            commentLabel.setText(comment[n]);


            //レビューダイアログ--------------------------------------------------------
            AppRate.with(this)
                    .setInstallDays(10) // インストールからの経過日数
                    .setLaunchTimes(10) // 起動回数
                    .setRemindInterval(1) // ｢後で評価する｣を押してから再び出すまでの経過日数
                    .setShowLaterButton(true) // 後で評価する｣を出すか
                    .setDebug(false) // default false
                    .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                        @Override
                        public void onClickButton(int which) {
                            Log.d(MainActivity.class.getName(), Integer.toString(which));
                        }
                    })
                    .monitor();

            // Show a dialog if meets conditions
            AppRate.showRateDialogIfMeetsConditions(this);
            //end-----------------------------------------------------------------------
        }
    }

    private void initZuckAd() {
        //_42fbb800ec   _8dddf8edfb
        mAdInterstitial = new AdInterstitial(this, "_42fbb800ec", new AdInterstitialListener() {
            @Override
            public void onReceiveAd() {
            }

            @Override
            public void onShowAd() {
                //一度zucksが表示された後はNendを表示させない
                nendFlag = 0;
            }

            @Override
            public void onCancelDisplayRate() {
            }

            @Override
            public void onTapAd() {
            }

            @Override
            public void onCloseAd() {
                // 再ロード
            }

            @Override
            public void onLoadFailure(Exception e) {
            }

            @Override
            public void onShowFailure(Exception e) {
                // 再ロード
                mAdInterstitial.load();
            }
        });
        mAdInterstitial.load();
    }

    //タイマークラス派生クラス------------------------------------------------------
    //run()に定周期で処理したい内容を記述
    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {

            //ここに定周期で実行したい処理を記述します
            Log.v("Log", "1秒タイマー実行");
            mHandler.post(new Runnable() {
                public void run() {
                    //ウォッチ表示メソッド実行
                    watchDateShow();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("LifeCycle", "onResume");
        //インタースティシャル広告表示メソッド実行
        showInterstitialAd();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("LifeCycle", "onPause");
        //Nend表示フラグリセット
        nendFlag = 1;
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        //タイマーのセット（一秒）-----------------------------------
        //タイマーインスタンス生成
        this.mainTimer = new Timer();
        //タスククラスインスタンス生成
        this.mainTimerTask = new MainTimerTask();
        //タイマースケジュール設定＆開始
        this.mainTimer.schedule(mainTimerTask, 0, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        //タイマー停止
        mainTimer.cancel();
    }

    //時計表示メソッド--------------------------------------------------------
    public void watchDateShow() {
        //public void watchDateShow() throws ParseException {
        //----------------------------------------------------------------------

        //保存されてるデータを読み込んで表示切り替え-----------------------------
        //Preferencesデータ読み込み
        int smokePrice_ja;
        float smokePrice_en;
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        int nsFlag = data.getInt("nsFlag", 1);
        int smokeNum = data.getInt("smokeNum", 1);
        String smokePriceCurrent = data.getString("smokePrice", "1");

        smokePrice_en = Float.parseFloat(smokePriceCurrent);
        smokePrice_ja = (int)smokePrice_en;

        String startDateStr = data.getString("startDate", "");

        Log.v("Log", "nsFlag" + nsFlag);
        Log.v("Log", "smokeNum" + smokeNum);
        Log.v("Log", "smokePrice" + smokePriceCurrent);
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
        String str1 = "";
        str1 = (stop_day + "day" + stop_hour_str + ":" + stop_min_str + ":" + stop_sec_str);
        String langJa = "ja";
        if (getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
            str1 = (stop_day + "日" + stop_hour_str + ":" + stop_min_str + ":" + stop_sec_str);
        }
        //テキストをテキストビューに表示
        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setText(str1);


        //吸わなかった本数の表示------------------------------------------------------

        //1日の喫煙数取得

        // 1本あたりの秒数
        long one_sec = (24 * 60 * 60) / smokeNum;
        // 吸わなかった本数
        long stop_num = since / one_sec;
        // 数値を3桁カンマ切りの文字列に整形する
        String str2 = String.format("%1$,3d", stop_num).replace(" ", "");
        //本を追加
        String str3 = (str2);
        if (getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
            str3 = (str2 + "本");
        }
        //表示
        TextView numLabel = (TextView) findViewById(R.id.numLabel);
        numLabel.setText(str3);


        //節約できた金額の表示------------------------------------------------------

        //1箱の値段取得

        // 1本あたりの金額
        float one_price_float = (float) smokePrice_en / 20;
        long one_price_long = smokePrice_ja / 20;
        // 節約できた金額
        float total_price_float = (float) one_price_float * stop_num;
        long total_price_long = (long) one_price_long * stop_num;
        // 数値を3桁カンマ切りの文字列に整形する

        String str4 = total_price_float > 0 ? String.format("%.2f", total_price_float).replace(" ", "") : "0";

        //円を追加
        String str5 = (str4);
        //表示
        if (getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
            str4 = String.format("%1$,3d", total_price_long).replace(" ", "");
            str5 = (str4 + "円");
        }
        TextView priceLabel = (TextView) findViewById(R.id.priceLabel);
        priceLabel.setText(str5);


        //伸びた寿命(1本あたり5分30秒(330秒)伸びる計算)の表示--------------------------

        long life_total_sec = stop_num * 330;

        //int life_sec = life_total_sec%60; //秒
        long life_min = (life_total_sec / 60) % 60;  //分
        long life_hour = (life_total_sec / 60 / 60) % 24;   //時
        long life_day = (life_total_sec / 60 / 60 / 24);   //日

        String longer_life = (life_day + "day" + life_hour + ":" + life_min);
        if (getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
            longer_life = (life_day + "日" + life_hour + "時間" + life_min + "分");
        }
        //表示
        TextView lifeLabel = (TextView) findViewById(R.id.lifeLabel);
        lifeLabel.setText(longer_life);

    }

    //リセットアラートメソッド--------------------------------------------------------
    public void resetAlert() {
        //---------------------------------------------------------------------------

        //リセット処理--------------------------------------------------------------
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //ダイアログタイトルをセット
        alert.setTitle(getString(R.string.text_title_reset));
        //ダイアログメッセージをセット
        alert.setMessage(getString(R.string.text_message_confirm_reset));
        // アラートダイアログのボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alert.setPositiveButton(getString(R.string.text_button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //OKボタンが押された時の処理
                reset();
                //Toast.makeText(MainActivity.this, "OK選択", Toast.LENGTH_LONG).show();
            }
        });
        // アラートダイアログのボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alert.setNegativeButton(getString(R.string.text_button_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //NOボタンが押された時の処理
                //Toast.makeText(MainActivity.this, "NO選択", Toast.LENGTH_LONG).show();
            }
        });
        //ダイアログ表示
        alert.show();
    }

    //リセットメソッド--------------------------------------------------------
    public void reset() {
        //---------------------------------------------------------------------------

        //Preferencesに、"nsFlag"というkeyでデータ保存
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor e = pref.edit();
        e.clear().commit();
        //e.putInt("nsFlag", 0);
        //e.commit();

        //セグエ
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }

    //Twitterボタンメソッド--------------------------------------------------------
    public void tweetBtn(View view) {
        //---------------------------------------------------------------------------
        tweet();
    }

    //Twitteメソッド--------------------------------------------------------
    public void tweet() {
        //---------------------------------------------------------------------------
        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        TextView numLabel = (TextView) findViewById(R.id.numLabel);
        TextView priceLabel = (TextView) findViewById(R.id.priceLabel);
        TextView lifeLabel = (TextView) findViewById(R.id.lifeLabel);
        String dateText = dateLabel.getText().toString();
        String numText = numLabel.getText().toString();
        String priceText = priceLabel.getText().toString();
        String lifeText = lifeLabel.getText().toString();

        String strMessage = "Total\n･Time:" + dateText +
                "\n･Number:" + numText +
                "\n･Money:" + priceText +
                "\n･Extended Longevity:" + lifeText + "\n";
        String strHashTag = "#NoSmokingWatch " + "\n";
        String strUrl = "http://nonsmo.jp";

        String strTweet = "";
        String langJa = "ja";
        if (getLanguage().toLowerCase().equals(langJa.toLowerCase())) {
            strMessage = "禁煙継続中！\n･禁煙時間：" + dateText +
                    "\n･禁煙できた本数：" + numText +
                    "\n･節約できた金額：" + priceText +
                    "\n･延びた寿命：" + lifeText + "\n";
            strHashTag = "#禁煙ウォッチ" + "\n";
            strUrl = "http://nonsmo.jp";
        }
        try {
            strTweet = "http://twitter.com/intent/tweet?text=" + URLEncoder.encode(strMessage, "UTF-8") + "+" + URLEncoder.encode(strHashTag, "UTF-8")
                    + "&url=" + URLEncoder.encode(strUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strTweet));
        startActivity(intent);
    }


    //FirebaseのSSPAndroidを取得-----------------------------------------------------
    public void sspLoad() {
        //---------------------------------------------------------------------------

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("SSPAndroid").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int activeFlag = dataSnapshot.child("activeFlag").getValue(int.class);
                        int nendPer = dataSnapshot.child("nendPer").getValue(int.class);
                        int percent = dataSnapshot.child("percent").getValue(int.class);
                        Log.v("Log", "SSPactiveFlag" + activeFlag);
                        Log.v("Log", "SSPnendPer" + nendPer);
                        Log.v("Log", "SSPpercent" + percent);

                        //Preferencesに、データ保存
                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor e = pref.edit();
                        e.putInt("SSPactiveFlag", activeFlag);
                        e.putInt("SSPnendPer", nendPer);
                        e.putInt("SSPpercent", percent);
                        e.commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //下記のTAGが""をつけないとエラーだったので、とりあえずつけて回避
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    //広告表示判別-----------------------------------------------------
    public void interNetwork() {
        //---------------------------------------------------------------------------

        //Preferencesデータ読み込み
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        int SSPactiveFlag = data.getInt("SSPactiveFlag", 1);
        int SSPnendPer = data.getInt("SSPnendPer", 1);
        int SSPpercent = data.getInt("SSPpercent", 1);

        if (SSPactiveFlag == 1) {

            Random ra = new Random();
            int randA = ra.nextInt(100) + 1;

            if (SSPpercent > randA) {

                //nendo表示確率に応じて表示----------------------------------------------
                //パーセントで表示切り替え
                Random rb = new Random();
                int randB = rb.nextInt(100) + 1;
                if (SSPnendPer > randB) {
                    //nendo広告表示--------------------------------------------
                    interNetwork = "nend";
                    //--------------------------------------------------------

                } else {
                    //zucks広告表示--------------------------------------------
                    interNetwork = "zucks";
                    //--------------------------------------------------------
                }

            } else {
                interNetwork = "";
            }

        } else {
            interNetwork = "";
        }
    }
    //-------------------------------広告表示判別end---------------------------------//

    //インタースティシャル広告表示メソッド--------------------------------------------------------
    public void showInterstitialAd() {
        //---------------------------------------------------------------------------

        interNetwork();   //NendかZucksか判別

        if (interNetwork.equals("nend")) {
            //nend広告表示--------------------------------------------
            showNendAd();
            //--------------------------------------------------------

        } else if (interNetwork.equals("zucks")) {

            //zucks広告表示--------------------------------------------
            mAdInterstitial.show();
        }

    }

    //Nend広告枠表示用メソッド--------------------------------------------------------
    public void showNendAd() {
        //---------------------------------------------------------------------------

        if (nendFlag == 1) {

            //Nend表示用のメソッド
            NendAdInterstitial.showAd(this);

            //Nend表示ステータス確認メソッド
            NendAdInterstitial.NendAdInterstitialShowResult result = NendAdInterstitial.showAd(this);
            switch (result) {
                case AD_SHOW_SUCCESS:
                    // 表示成功
                    Log.v("Log", "Nend表示:表示成功");
                    nendFlag = 0;
                    break;
                case AD_LOAD_INCOMPLETE:
                    // ロードが完了していない
                    Log.v("Log", "Nend表示:ロードが完了していない");
                    break;
                case AD_REQUEST_INCOMPLETE:
                    // 抽選が正常完了していない
                    Log.v("Log", "Nend表示:抽選が正常完了していない");
                    break;
                case AD_DOWNLOAD_INCOMPLETE:
                    // WebViewのロードが完了していない
                    Log.v("Log", "Nend表示:WebViewのロードが完了していない");
                    break;
                case AD_FREQUENCY_NOT_RECHABLE:
                    // フリークエンシーカウントに到達していない
                    Log.v("Log", "Nend表示:フリークエンシーカウントに到達していない");
                    break;
                case AD_SHOW_ALREADY:
                    // 既に表示されている
                    Log.v("Log", "Nend表示:既に表示されている");
                    nendFlag = 0;
                    break;
            }
        }
    }

    //下記初期設定どおり---------------------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

        } else if (id == R.id.nav_gallery) {
            //開始時間変更
            startActivity(new Intent(MainActivity.this, EditDateActivity.class));

        } else if (id == R.id.nav_slideshow) {
            //禁煙カルテ変更
            startActivity(new Intent(MainActivity.this, EditKarteActivity.class));

        } else if (id == R.id.nav_manage) {
            //リセットアラート
            resetAlert();

        }/* else if (id == R.id.nav_share) {

        }*/ else if (id == R.id.nav_send) {
            tweet();

        } else if (id == R.id.nav_ec) {
            //URLセット
            Uri uri = Uri.parse("http://hb.afl.rakuten.co.jp/hgc/1093d022.4ae092b0.1093d023.04cc3338/?pc=http%3A%2F%2Franking.rakuten.co.jp%2Fdaily%2F555000%2F&m=http%3A%2F%2Franking.rakuten.co.jp%2Fdaily%2F555000%2F&scid=af_url_txt&link_type=text&ut=eyJwYWdlIjoidXJsIiwidHlwZSI6InRlc3QiLCJjb2wiOjAsInRhciI6MX0%3D");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
