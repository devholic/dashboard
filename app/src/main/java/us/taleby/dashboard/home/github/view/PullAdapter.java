package us.taleby.dashboard.home.github.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.joda.time.DateTime;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import us.taleby.dashboard.R;
import us.taleby.dashboard.home.github.data.GithubData;

/**
 * Created by devholic on 15. 12. 26..
 */

public class PullAdapter extends RecyclerView.Adapter {

    ArrayList<GithubData> pull;
    Context context;
    int open, merged, closed;

    public PullAdapter(Context context, ArrayList<GithubData> pull) {
        this.context = context;
        this.pull = pull;
        open = context.getResources().getColor(R.color.statusOpen);
        merged = context.getResources().getColor(R.color.statusMerged);
        closed = context.getResources().getColor(R.color.statusClosed);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_github_list_item, parent, false);
        return new DataHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DataHolder ph = (DataHolder) holder;
        GithubData pr = pull.get(position);
        ph.title.setText(pr.getTitle());
        ph.user.setText(pr.getRepository() + " " + pr.getNum() + " | " + pr.getUser());
        DateTime created = new DateTime(pr.getCreatedAt());
        String time = String.format("%s (%02d:%02d)",
                DateUtils.getRelativeTimeSpanString(pr.getCreatedAt()),
                created.getHourOfDay(), created.getMinuteOfHour()
        );
        ph.time.setText(time);
        if (pr.isMerged()) {
            ph.status.setBackgroundColor(merged);
        } else {
            switch (pr.getState()) {
                case "open":
                    ph.status.setBackgroundColor(open);
                    break;
                case "closed":
                    ph.status.setBackgroundColor(closed);
                    break;
            }
        }
        Glide.with(context).load(pr.getUserImg())
                .bitmapTransform(new RoundedCornersTransformation(context, 8, 0))
                .into(ph.userImg);
    }

    @Override
    public int getItemCount() {
        return pull.size();
    }
}

