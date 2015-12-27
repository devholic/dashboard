package us.taleby.dashboard.home;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import us.taleby.dashboard.R;

/**
 * Created by devholic on 15. 12. 26..
 */
public class DashboardApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/nanum_ul.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        JodaTimeAndroid.init(this);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone("Asia/Seoul");
    }
}
