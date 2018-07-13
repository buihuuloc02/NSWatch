package self.edu.nswatch.dummy;

import android.app.Application;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by Administrator on 7/13/2018.
 */

public class NoSmokingWatchApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }
}
