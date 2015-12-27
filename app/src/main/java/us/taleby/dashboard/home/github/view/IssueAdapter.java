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

public class IssueAdapter extends RecyclerView.Adapter {

    ArrayList<GithubData> issues;

    Context context;
    int open, closed;

    public IssueAdapter(Context context, ArrayList<GithubData> issues) {
        this.context = context;
        this.issues = issues;
        open = context.getResources().getColor(R.color.statusOpen);
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
        GithubData issue = issues.get(position);
        ph.title.setText(issue.getTitle());
        ph.user.setText(issue.getRepository() + " " + issue.getNum() + " | " + issue.getUser());
        DateTime created = new DateTime(issue.getCreatedAt());
        String time = String.format("%s (%02d:%02d)",
                DateUtils.getRelativeTimeSpanString(issue.getCreatedAt()),
                created.getHourOfDay(), created.getMinuteOfHour()
        );
        ph.time.setText(time);
        switch (issue.getState()) {
            case "open":
                ph.status.setBackgroundColor(open);
                break;
            case "closed":
                ph.status.setBackgroundColor(closed);
                break;
        }
        Glide.with(context).load(issue.getUserImg())
                .bitmapTransform(new RoundedCornersTransformation(context, 8, 0))
                .into(ph.userImg);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }
}

