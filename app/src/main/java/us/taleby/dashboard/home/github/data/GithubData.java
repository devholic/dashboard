package us.taleby.dashboard.home.github.data;

/**
 * Created by devholic on 15. 12. 26..
 */
public class GithubData {
    String repository;
    String user, user_img;
    String num, title;
    String state; // open | closed
    boolean isMerged;
    long created_at;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserImg() {
        return user_img;
    }

    public void setUserImg(String user_img) {
        this.user_img = user_img;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setIsMerged(boolean isMerged) {
        this.isMerged = isMerged;
    }

    public long getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(long created_at) {
        this.created_at = created_at;
    }
}
