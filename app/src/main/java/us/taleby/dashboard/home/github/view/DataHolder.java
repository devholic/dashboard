package us.taleby.dashboard.home.github.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import us.taleby.dashboard.R;

/**
 * Created by devholic on 15. 12. 26..
 */
public class DataHolder extends RecyclerView.ViewHolder {

    public ImageView userImg;
    public TextView title, user, time;
    public RelativeLayout status;

    public DataHolder(View itemView) {
        super(itemView);
        userImg = (ImageView) itemView.findViewById(R.id.userImg);
        title = (TextView) itemView.findViewById(R.id.title);
        time = (TextView) itemView.findViewById(R.id.time);
        user = (TextView) itemView.findViewById(R.id.user);
        status = (RelativeLayout) itemView.findViewById(R.id.status);
    }
}
