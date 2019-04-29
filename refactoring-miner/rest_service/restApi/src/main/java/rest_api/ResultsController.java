package rest_api;

import java.io.*; 
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultsController {
    @RequestMapping("/results")
    public String results() {
        String filepath;
        if(Parameters.getCommit1().length() == 0 || Parameters.getCommit2().length() == 0){
            filepath = "/mining_repo/all_refactorings.csv";
        }
        else
        {
            filepath = String.format("/mining_repo/refactorings_%s_%s.csv", Parameters.getCommit1(), Parameters.getCommit2());
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