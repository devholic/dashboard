package us.taleby.dashboard.home.github;

/**
 * Created by devholic on 15. 12. 26..
 */
public class GithubResources {

    public static final String GITHUB_ORG_NAME = "GITHUB_ORG_NAME";
    public static final String GITHUB_ID = "APP_ID";
    public static final String GITHUB_SECRET = "APP_SECRET";
    public static final String GITHUB_REPO = "https://api.github.com/repos/" + GITHUB_ORG_NAME;
    public static final String GITHUB_ORG = "https://api.github.com/orgs/" + GITHUB_ORG_NAME
            + "/repos?client_id=" + GITHUB_ID + "&client_secret=" + GITHUB_SECRET;

    public static String issueUrl(String repo) {
        return GITHUB_REPO + "/" + repo + "/issues?client_id="
                + GITHUB_ID + "&client_secret=" + GITHUB_SECRET + "&state=all";
    }

    public static String pullUrl(String repo) {
        return GITHUB_REPO + "/" + repo + "/pulls?client_id="
                + GITHUB_ID + "&client_secret=" + GITHUB_SECRET + "&state=all";
    }

}
