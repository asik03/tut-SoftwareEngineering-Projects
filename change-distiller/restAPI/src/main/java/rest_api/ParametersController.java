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
    public String parameters(@RequestParam(value="repo", defaultValue="") String repo,  @RequestParam(value="commit1", defaultValue="") String commit1, @RequestParam(value="commit2", defaultValue="") String commit2) {
		
        Logger logger = Logger.getAnonymousLogger();
        
        if(Status.getExecuting()){
            return String.format("Already extracting source code changes in repo %s", repo);
        }

        try{
            Status.setReady(false);
            Status.setExecuting(true);
            
            // GitExecuter object
            rest_api.GitExecuter executer = new rest_api.GitExecuter();
            
            // Setting the parameters
            Parameters.setRepo(repo);
            Parameters.setCommit1(commit1);
            Parameters.setCommit2(commit2);
            
            // Calling the GitExecuter method to analyze the repos.
            executer.callExecuter(Parameters.getRepo(), Parameters.getCommit1() , Parameters.getCommit2());

            Status.setReady(true);
            Status.setExecuting(false);

        } catch (Exception ex) {
			logger.log(Level.SEVERE, "an exception was thrown", ex);
            Status.setExecuting(false);
            return String.format("Error analyzing Issues for repo %s", repo);
        } 
        return String.format("Analyzed Issues for repo %s", repo);

    }


}