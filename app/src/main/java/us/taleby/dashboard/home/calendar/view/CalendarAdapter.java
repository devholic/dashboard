package us.taleby.dashboard.home.calendar.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;

import us.taleby.dashboard.R;
import us.taleby.dashboard.home.calendar.data.CalendarData;

/**
 * Created by devholic on 15. 12. 26..
 */

public class CalendarAdapter extends RecyclerView.Adapter {

    ArrayList<CalendarData> eventList;
    Context context;
    int future, past;

    public CalendarAdapter(Context context, ArrayList<CalendarData> events) {
        this.eventList = events;
        future = context.getResources().getColor(R.color.textColorPrimaryDark);
        past = context.getResources().getColor(R.color.textColorDisabledDark);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_calendar_list_item, parent, false);
        return new DataHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DataHolder ph = (DataHolder) holder;
        CalendarData event = eventList.get(position);
        ph.title.setText(event.getSummary());
        if (event.getLocation() != null) {
            ph.location.setText(event.getLocation());
        } else {
            ph.location.setVisibility(View.GONE);
        }
        if (event.isFutureEvent()) {
            ph.status.setBackgroundColor(future);
        } else {
            ph.status.setBackgroundColor(past);
        }
        DateTime start = new DateTime(event.getStart());
        DateTime end = new DateTime(event.getEnd());
        String time = String.format("%s (%02d:%02d ~ %02d:%02d)",
                DateUtils.getRelativeTimeSpanString(start.getMillis()).toString(),
                start.getHourOfDay(), start.getMinuteOfHour(),
                end.getHourOfDay(), end.getMinuteOfHour());
        ph.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

