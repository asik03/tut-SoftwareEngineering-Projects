package rest_api;

import java.io.*; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class ParametersController {


    @RequestMapping("/parameters")
    public String parameters(@RequestParam(value="repo", defaultValue="World") String repo, @RequestParam(value="commit1", defaultValue="") String commit1, @RequestParam(value="commit2", defaultValue="") String commit2) {
		Logger logger = Logger.getAnonymousLogger();
        if(Status.getExecuting()){
            return String.format("Already analyzing refactorings for repo %s", repo);
        }
        //Analyze all commits
        if(commit1.length() == 0 || commit2.length() == 0){
            try{
                Status.setReady(false);
                Status.setExecuting(true);
                Process process = Runtime.getRuntime().exec(String.format("/mine.sh %s", repo));

                Parameters.repo = repo;
                Parameters.commit1 = commit1;
                Parameters.commit2 = commit2;

                BufferedReader in = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    logger.log(line);
                }
                //Copy results to volume
                String filepath = "/mining_repo/all_refactorings.csv";
                String repoName = repo.substring(repo.lastIndexOf('/') + 1);
                Runtime.getRuntime().exec(String.format("cp %s /results/%s_all_refactorings.csv", filepath, repoName));

                Status.setReady(true);
                Status.setExecuting(false);
            } catch (Exception ex) {
				logger.log(Level.SEVERE, "an exception was thrown", ex);
                 Status.setExecuting(false);
            }

        //Analyze from beginCommit to endCommit
        }else {
            try{
                Status.setReady(false);
                Status.setExecuting(true);
                Process process = Runtime.getRuntime().exec(String.format("/mine.sh %s %s %s", repo, commit1, commit2));
                
                Parameters.repo = repo;
                Parameters.commit1 = commit1;
                Parameters.commit2 = commit2;

                BufferedReader in = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    logger.log(line);
                }
                //Copy results to volume
                String filepath =  String.format("/mining_repo/refactorings_%s_%s.csv", commit1, commit2);
                String repoName = repo.substring(repo.lastIndexOf('/') + 1);
                Runtime.getRuntime().exec(String.format("cp %s /results/%s_refactorings_%s_%s.csv", filepath, repoName, commit1, commit2));

                Status.setReady(true);
                Status.setExecuting(false);
            } catch (Exception ex) {
				logger.log(Level.SEVERE, "an exception was thrown", ex);
                 Status.setExecuting(false);
            }
        }

        //Return result to client
        

        return String.format("Analyzed Refactorings for repo %s", repo);

    }


}