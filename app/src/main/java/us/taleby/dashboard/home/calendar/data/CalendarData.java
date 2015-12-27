package us.taleby.dashboard.home.calendar.data;

/**
 * Created by devholic on 15. 12. 28..
 */
public class CalendarData {

    boolean futureEvent;
    long start, end;
    String location, summary;

    public boolean isFutureEvent() {
        return futureEvent;
    }

    public void setFutureEvent(boolean futureEvent) {
        this.futureEvent = futureEvent;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
