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
    public String parameters(@RequestParam(value="repo", defaultValue="") String repo, @RequestParam(value="tracker", defaultValue="") String issueTracker, @RequestParam(value="code", defaultValue="") String issueCode, @RequestParam(value="from", defaultValue="") String commit1, @RequestParam(value="commit2", defaultValue="") String commit2) {
		Logger logger = Logger.getAnonymousLogger();
        if(Status.getExecuting()){
            return String.format("Already analyzing issues for repo %s", repo);
        }

        //Analyze all commits
        if(commit1.length() == 0){
            try{
                Status.setReady(false);
                Status.setExecuting(true);
                Process process = Runtime.getRuntime().exec(String.format("/SZZUnleashed/szz.sh %s %s %s", issueTracker, repo, issueCode));
                new Parameters(issueTracker, repo, commit1);
                BufferedReader in = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    logger.log(line);
                }
                //Copy results to volume
                String filepath = "/SZZUnleashed/szz/results/fix_and_introducers_pairs.json";
                String repoName = repo.substring(repo.lastIndexOf('/') + 1);
                Runtime.getRuntime().exec(String.format("cp %s /results/%s_fix_and_introducers_pairs.json", filepath, repoName));

                Status.setReady(true);
                Status.setExecuting(false);
            } catch (Exception ex) {
				logger.log(Level.SEVERE, "an exception was thrown", ex);
                 Status.setExecuting(false);
                 return String.format("Error analyzing Issues for repo %s", repo);
            }

        //Analyze from beginCommit to endCommit
        }else {
            try{
                Status.setReady(false);
                Status.setExecuting(true);
                Process process = Runtime.getRuntime().exec(String.format("/SZZUnleashed/szz.sh %s %s %s %s", issueTracker, repo, issueCode, commit1));
                new Parameters(issueTracker, repo, commit1);
                BufferedReader in = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    logger.log(line);
                }
                //Copy results to volume
                String filepath = "/SZZUnleashed/szz/results/fix_and_introducers_pairs.json";
                String repoName = repo.substring(repo.lastIndexOf('/') + 1);
                Runtime.getRuntime().exec(String.format("cp %s /results/%s_fix_and_introducers_pairs_%s.csv", filepath, repoName, commit1));

                Status.setReady(true);
                Status.setExecuting(false);
            } catch (Exception ex) {
				logger.log(Level.SEVERE, "an exception was thrown", ex);
                 Status.setExecuting(false);
                 return String.format("Error analyzing Issues for repo %s", repo);

            }
        }

        //Return result to client
        

        return String.format("Analyzed Issues for repo %s", repo);

    }


}