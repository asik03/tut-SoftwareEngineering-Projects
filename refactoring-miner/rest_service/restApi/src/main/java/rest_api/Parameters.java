package rest_api;

public class Parameters {
    private static String repo;
    private static String commit1;
    private static String commit2;

    private Parameters(){

    }
    public static String getRepo() {
        return repo;
    }
	public static String getCommit1() {
        return commit1;
    }
	public static String getCommit2() {
		return commit2;
	}
}