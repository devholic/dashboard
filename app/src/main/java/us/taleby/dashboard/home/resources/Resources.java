package us.taleby.dashboard.home.resources;

/**
 * Created by devholic on 15. 12. 27..
 */
public class Resources {
    public static String[] fragmentList = {"github", "github", "calendar", "calendar"};

    public static String getFragmentTag(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }
}
