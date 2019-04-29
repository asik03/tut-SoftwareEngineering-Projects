package rest_api;

import java.io.*; 
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;


@RestController
public class ResultsController {
    @RequestMapping("/results")
    public String results() {
        String filepath;
        String repo = Parameters.getRepo();
        String repoName = repo.substring(repo.lastIndexOf('/') + 1);

        Logger logger = Logger.getAnonymousLogger();

        if(Parameters.getCommit1().length() == 0 || Parameters.getCommit2().length() == 0){
            filepath = String.format("/change-distiller/results/%s_results.csv", repoName);
        }
        else
        {
            filepath = String.format("/change-distiller/results/results_%s_%s_%s.csv", repoName, Parameters.getCommit1(), Parameters.getCommit2());
        }

        File f = new File(filepath);

        if(Status.getReady()) {
            try{
                return new String(Files.readAllBytes(f.toPath()));
            }catch (Exception ex) {
                return "Error";
            }
        }else{
            return "Not ready";
        }
    }
}