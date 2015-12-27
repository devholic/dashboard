package us.taleby.dashboard.home.calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.taleby.dashboard.R;
import us.taleby.dashboard.home.calendar.data.CalendarData;
import us.taleby.dashboard.home.calendar.view.CalendarAdapter;
import us.taleby.dashboard.firetv.OnRemotePressedListener;
import us.taleby.dashboard.network.NetworkManager;

/**
 * Created by devholic on 15. 12. 27..
 */
public class FutureEventFragment extends Fragment {

    ArrayList<CalendarData> eventList;
    LinearLayoutManager eventLayoutManager;

    boolean isUpdating = false;

    @Bind(R.id.page_name)
    TextView pageName;
    @Bind(R.id.wheel)
    ProgressWheel wheel;
    @Bind(R.id.recyclerView)
    RecyclerView eventView;

    Comparator<CalendarData> comparator;

    OnRemotePressedListener remoteCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_pager_item_default, container, false);
        ButterKnife.bind(this, view);
        setResources();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            remoteCallback = (OnRemotePressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must imeplement OnRemotePressedListener");
        }
    }

    public void handleKeyEvent(int key) {
        switch (key) {
            case KeyEvent.KEYCODE_DPAD_UP:
                scrollRecyclerView(true);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                scrollRecyclerView(false);
                break;
            case KeyEvent.KEYCODE_MENU:
                updateData();
                break;
        }
    }

    public void scrollRecyclerView(boolean isUp) {
        if (isUp) {
            int vMin = eventLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (vMin - 1 >= 0) {
                eventView.smoothScrollToPosition(vMin - 1);
            }
        } else {
            int max = eventList.size();
            int vMax = eventLayoutManager.findLastCompletelyVisibleItemPosition();
            if (vMax + 1 < max) {
                eventView.smoothScrollToPosition(vMax + 1);
            }
        }
    }

    private void setResources() {
        pageName.setText("coming up");
        // Comparator
        comparator = (CalendarData c1, CalendarData c2) ->
                c1.getStart() < c2.getStart() ?
                        -1 : c1.getStart() > c2.getStart() ?
                        1 : 0;
        // Event
        eventLayoutManager = new LinearLayoutManager(getContext());
        eventLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventList = new ArrayList<>();
        eventView.setAdapter(new CalendarAdapter(getContext(), eventList));
        eventView.setLayoutManager(eventLayoutManager);
        updateData();
    }

    public void updateData() {
        if (!isUpdating) {
            isUpdating = true;
            wheel.setVisibility(View.VISIBLE);
            eventView.setVisibility(View.INVISIBLE);
            eventList = new ArrayList<>();
            new FetchTask().execute();
        }
    }

    private void filterData() {
        DateTime now = DateTime.now();
        for (int i = eventList.size() - 1; i >= 0; i--) {
            DateTime start = new DateTime(eventList.get(i).getStart())
                    .toDateTime(DateTimeZone.forOffsetHours(9));
            if (now.getMillis() > start.getMillis()) {
                eventList.remove(i);
            }else{
                eventList.get(i).setFutureEvent(true);
            }
        }
        Collections.sort(eventList, comparator);
        eventView.setAdapter(new CalendarAdapter(getContext(), eventList));
        wheel.clearAnimation();
        wheel.setVisibility(View.INVISIBLE);
        eventView.setVisibility(View.VISIBLE);
        isUpdating = false;
    }

    private class FetchTask extends AsyncTask<Void, Void, ArrayList<CalendarData>> {

        @Override
        protected ArrayList<CalendarData> doInBackground(Void... v) {
            eventList = new ArrayList<>();
            Request request = new Request.Builder()
                    .url(CalendarResources.ical)
                    .build();
            try {
                Response response = NetworkManager.getHttpClient().newCall(request).execute();
                if (response.code() == 200) {
                    // Parsed Event
                    CalendarData event = new CalendarData();
                    // DateFormat
                    DateTimeFormatter dateF = DateTimeFormat.forPattern("yyyyMMdd");
                    DateTimeFormatter timeF = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ");
                    // Parse
                    InputStream in = response.body().byteStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    boolean isEvent = false; // check if current data is in event
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("BEGIN:VEVENT")) {
                            event = new CalendarData();
                            isEvent = true;
                        } else if (line.equals("END:VEVENT")) {
                            eventList.add(event);
                            isEvent = false;
                        } else {
                            if (isEvent) {
                                String[] data = line.split(":");
                                if (data.length == 2) {
                                    switch (data[0]) {
                                        case "DTSTART":
                                            DateTime start = DateTime.parse(data[1], timeF);
                                            event.setStart(start.getMillis());
                                            break;
                                        case "DTSTART;VALUE=DATE":
                                            DateTime startDate = DateTime.parse(data[1], dateF)
                                                    .withZone(DateTimeZone.UTC);
                                            event.setStart(startDate.getMillis());
                                            break;
                                        case "DTEND":
                                            DateTime end = DateTime.parse(data[1], timeF);
                                            event.setEnd(end.getMillis());
                                            break;
                                        case "DTEND;VALUE=DATE":
                                            DateTime endDate = DateTime.parse(data[1], dateF)
                                                    .withZone(DateTimeZone.UTC);
                                            event.setEnd(endDate.getMillis());
                                            break;
                                        case "LOCATION":
                                            event.setLocation(data[1]);
                                            break;
                                        case "SUMMARY":
                                            event.setSummary(data[1]);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eventList;
        }

        @Override
        protected void onPostExecute(ArrayList<CalendarData> eventList) {
            filterData();
        }
    }
}
