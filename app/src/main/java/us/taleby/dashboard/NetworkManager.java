package us.taleby.dashboard;

import com.squareup.okhttp.OkHttpClient;

/**
 * Created by devholic on 15. 12. 26..
 */
public class NetworkManager {

    private static OkHttpClient httpClient;


    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
        }
        return httpClient;
    }

}
