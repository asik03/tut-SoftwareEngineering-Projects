package rest_api;

import java.io.*; 
import java.nio.file.Files;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultsController {
    @RequestMapping("/results")
    public String results() {
        String filepath = "/SZZUnleashed/szz/results/fix_and_introducers_pairs.json";

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