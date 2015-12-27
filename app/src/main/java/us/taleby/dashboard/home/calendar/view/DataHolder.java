package us.taleby.dashboard.home.calendar.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import us.taleby.dashboard.R;

/**
 * Created by devholic on 15. 12. 26..
 */
public class DataHolder extends RecyclerView.ViewHolder {

    public TextView title, location, time;
    public RelativeLayout status;

    public DataHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        time = (TextView) itemView.findViewById(R.id.time);
        location = (TextView) itemView.findViewById(R.id.location);
        status = (RelativeLayout) itemView.findViewById(R.id.status);
    }
}
