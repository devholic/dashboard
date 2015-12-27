package us.taleby.dashboard.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextClock;
import android.widget.TextView;

import com.viewpagerindicator.LinePageIndicator;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import us.taleby.dashboard.R;
import us.taleby.dashboard.home.calendar.FutureEventFragment;
import us.taleby.dashboard.home.calendar.WeeklyEventFragment;
import us.taleby.dashboard.home.github.GithubIssueFragment;
import us.taleby.dashboard.home.github.GithubPullFragment;
import us.taleby.dashboard.home.resources.Resources;
import us.taleby.dashboard.firetv.OnRemotePressedListener;

/**
 * Created by devholic on 15. 12. 26..
 */
public class Home extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        OnRemotePressedListener {

    @Bind(R.id.clock)
    TextClock clock;
    @Bind(R.id.section_name)
    TextView sectionName;
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.indicator)
    LinePageIndicator indicator;

    Timer timer;
    boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ButterKnife.bind(this);
        setResources();
    }

    private void setResources() {
        pager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        pager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        pager.addOnPageChangeListener(this);
        indicator.setViewPager(pager);
        sectionName.setText(Resources.fragmentList[0]);
        clock.setFormat12Hour("MMMM dd, hh:mm");
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                int position = pager.getCurrentItem();
                if (position != Resources.fragmentList.length - 1) {
                    pager.setCurrentItem(position + 1);
                } else {
                    pager.setCurrentItem(0);
                }
                position = pager.getCurrentItem();
                switch (pager.getCurrentItem()) {
                    case 0:
                        // Position 0
                        GithubPullFragment gpf = (GithubPullFragment) getSupportFragmentManager()
                                .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                        gpf.updateData();
                        break;
                    case 1:
                        // Position 1
                        GithubIssueFragment gif = (GithubIssueFragment) getSupportFragmentManager()
                                .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                        gif.updateData();
                        break;
                    case 2:
                        // Position 2
                        FutureEventFragment fef = (FutureEventFragment) getSupportFragmentManager()
                                .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                        fef.updateData();
                        break;
                    case 3:
                        // Position 3
                        WeeklyEventFragment wef = (WeeklyEventFragment) getSupportFragmentManager()
                                .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                        wef.updateData();
                        break;
                }
            }
        };
        if (!isTimerRunning) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }, 4 * 60 * 1000, 4 * 60 * 1000);
            isTimerRunning = true;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MENU:
                handled = true;
                onRemotePressed(keyCode);
                break;
            default:
        }
        return handled || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRemotePressed(int key) {
        int position = pager.getCurrentItem();
        switch (position) {
            case 0:
                GithubPullFragment gpf = (GithubPullFragment) getSupportFragmentManager()
                        .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                gpf.handleKeyEvent(key);
                break;
            case 1:
                GithubIssueFragment gif = (GithubIssueFragment) getSupportFragmentManager()
                        .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                gif.handleKeyEvent(key);
                break;
            case 2:
                FutureEventFragment fef = (FutureEventFragment) getSupportFragmentManager()
                        .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                fef.handleKeyEvent(key);
                break;
            case 3:
                WeeklyEventFragment wef = (WeeklyEventFragment) getSupportFragmentManager()
                        .findFragmentByTag(Resources.getFragmentTag(pager.getId(), position));
                wef.handleKeyEvent(key);
                break;
        }
    }

    // ViewPager

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        sectionName.setText(Resources.fragmentList[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // Github Pull requests
                    return new GithubPullFragment();
                case 1:
                    // Github Issues
                    return new GithubIssueFragment();
                case 2:
                    // Future events
                    return new FutureEventFragment();
                case 3:
                    // Weekly events
                    return new WeeklyEventFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return Resources.fragmentList.length;
        }

        @Override
        public float getPageWidth(int position) {
            return 0.4f;
        }
    }

    @Override
    protected void onDestroy() {
        if (isTimerRunning) {
            timer.cancel();
            timer.purge();
            timer = null;
            isTimerRunning = false;
        }
        super.onDestroy();
    }
}
