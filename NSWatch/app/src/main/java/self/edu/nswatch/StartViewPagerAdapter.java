package self.edu.nswatch;

import self.edu.nswatch.StartViewActivity1;
import self.edu.nswatch.StartViewActivity2;
import self.edu.nswatch.StartViewActivity3;
import self.edu.nswatch.StartViewActivity4;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class StartViewPagerAdapter extends FragmentStatePagerAdapter {
    public StartViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StartViewActivity1();
            case 1:
                return new StartViewActivity2();
            case 2:
                return new StartViewActivity3();
            default:
                return new StartViewActivity4();
        }
    }

    // ページ数
    @Override
    public int getCount() {
        return 4;
    }

    /*
    // タブタイトル
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Left View";
            case 1:
                return "Center View";
            case 2:
                return "Right View";
        }
        return "";
    }
    */
}
