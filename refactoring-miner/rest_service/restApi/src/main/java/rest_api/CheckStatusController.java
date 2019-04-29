package rest_api;

import java.io.*; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckStatusController {
    @RequestMapping("/status")
    public String status() {
        if(Status.getReady()) {
            return "Results are ready";
        }else{
            return "Not ready";
        }
    }
}