package rest_api;

public class Parameters {
    private static String issueTracker;
    private static String repo;
    private static String commit1;

    private Parameters() {

    }
    public static String getRepo() {
        return repo;
    }
	public static String getIssueTracker() {
        return issueTracker;
    }
	public static String getCommit1() {
		return commit1;
	}
}