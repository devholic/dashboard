package us.taleby.dashboard.home.github;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.taleby.dashboard.R;
import us.taleby.dashboard.home.github.data.GithubData;
import us.taleby.dashboard.home.github.view.PullAdapter;
import us.taleby.dashboard.firetv.OnRemotePressedListener;
import us.taleby.dashboard.network.NetworkManager;

/**
 * Created by devholic on 15. 12. 26..
 */
public class GithubPullFragment extends Fragment {

    ArrayList<GithubData> pullList;
    LinearLayoutManager pLayoutManager;

    int count = -1;
    boolean isUpdating = false;

    @Bind(R.id.page_name)
    TextView pageName;
    @Bind(R.id.wheel)
    ProgressWheel wheel;
    @Bind(R.id.recyclerView)
    RecyclerView prView;

    Comparator<GithubData> comparator;

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
            int vMin = pLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (vMin - 1 >= 0) {
                prView.smoothScrollToPosition(vMin - 1);
            }
        } else {
            int max = pullList.size();
            int vMax = pLayoutManager.findLastCompletelyVisibleItemPosition();
            if (vMax + 1 < max) {
                prView.smoothScrollToPosition(vMax + 1);
            }
        }
    }

    private void setResources() {
        pageName.setText("pull requests");
        // Comparator
        comparator = (GithubData pr1, GithubData pr2) ->
                pr1.getCreatedAt() > pr2.getCreatedAt() ?
                        -1 : pr1.getCreatedAt() < pr2.getCreatedAt() ?
                        1 : 0;
        // PullRequest
        pLayoutManager = new LinearLayoutManager(getContext());
        pLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pullList = new ArrayList<>();
        prView.setAdapter(new PullAdapter(getContext(), pullList));
        prView.setLayoutManager(pLayoutManager);
        updateData();
    }

    public void updateData() {
        if (!isUpdating) {
            isUpdating = true;
            wheel.setVisibility(View.VISIBLE);
            prView.setVisibility(View.INVISIBLE);
            pullList = new ArrayList<>();
            FetchTask fetch = new FetchTask();
            fetch.execute();
        }
    }

    private void updateLoadStatus(boolean isSuccess) {
        count--;
        if (count == 0) {
            Collections.sort(pullList, comparator);
            prView.setAdapter(new PullAdapter(getContext(), pullList));
            wheel.clearAnimation();
            wheel.setVisibility(View.INVISIBLE);
            prView.setVisibility(View.VISIBLE);
            isUpdating = false;
        }
    }

    private class FetchTask extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... v) {
            JSONArray repos = null;
            Request request = new Request.Builder()
                    .url(GithubResources.GITHUB_ORG)
                    .build();
            try {
                Response response = NetworkManager.getHttpClient().newCall(request).execute();
                if (response.code() == 200) {
                    repos = new JSONArray(response.body().string());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return repos;
        }

        @Override
        protected void onPostExecute(JSONArray repos) {
            if (repos != null) {
                try {
                    count = repos.length();
                    for (int i = 0; i < repos.length(); i++) {
                        JSONObject repo = repos.getJSONObject(i);
                        FetchPullTask pullTask = new FetchPullTask();
                        pullTask.execute(repo.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
    }

    private class FetchPullTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String name = params[0];
            Request request = new Request.Builder()
                    .url(GithubResources.pullUrl(params[0]))
                    .build();
            try {
                Response response = NetworkManager.getHttpClient().newCall(request).execute();
                if (response.code() == 200) {
                    JSONArray pulls = new JSONArray(response.body().string());
                    for (int i = 0; i < pulls.length(); i++) {
                        JSONObject pull = pulls.getJSONObject(i);
                        GithubData pr = new GithubData();
                        pr.setRepository(name);
                        pr.setNum("#" + Integer.toString(pull.getInt("number")));
                        pr.setTitle(pull.getString("title"));
                        pr.setUser(pull.getJSONObject("user").getString("login"));
                        pr.setUserImg(pull.getJSONObject("user").getString("avatar_url"));
                        if (!pull.has("merged_at") || pull.isNull("merged_at")) {
                            pr.setIsMerged(false);
                        } else {
                            pr.setIsMerged(true);
                        }
                        pr.setState(pull.getString("state"));
                        DateTime date = DateTime.parse(pull.getString("created_at"));
                        pr.setCreatedAt(date.getMillis());
                        pullList.add(pr);
                    }
                    return true;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            updateLoadStatus(isSuccess);
        }

    }

}
