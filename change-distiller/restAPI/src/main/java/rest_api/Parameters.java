package rest_api;

public class Parameters {
    private static String repo;
    private static String commit1;
    private static String commit2;

    private Parameters() {

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


    public static void setRepo(String repo){
        Parameters.repo = repo;
    }

    public static void setCommit1(String commit1){
        Parameters.commit1 = commit1;
    }

    public static void setCommit2(String commit2){
        Parameters.commit2 = commit2;
    }

}
