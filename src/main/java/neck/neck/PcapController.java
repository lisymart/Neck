package neck.neck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@SpringBootApplication
@EnableAsync
public class PcapController {    
	@Autowired
	private LogstashProcessService lps;
   
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    	public String process(HttpServletRequest request,@RequestParam final String addition) 
    			throws ServletException, IOException, InterruptedException {
            Map<String, String[]> params = request.getParameterMap();
            final TreeMap<String, String> change = new TreeMap<String, String>();
            ArrayList<String> fileNames = new ArrayList<>();
            
            
            for (String attName : params.keySet()){
            	if (attName.endsWith(".pcap") || attName.endsWith(".csv") || attName.endsWith(".log")){
            		fileNames.add(attName);
            	} else {
            		change.put(attName, params.get(attName)[0]);
            	}
            }
            change.remove("addition");
        
        List<Future<String>> results = new ArrayList<>();
        
        for (String name : fileNames){
        	File folder = new File("data/pendings/" + name);
        	for (File fileName : folder.listFiles()){        
        		results.add(lps.process(fileName, change, addition));
        	}
        }
        
        boolean test = false;
        while (!test){
        	test = true;
        	for (Future<String> wait : results){
        		if (wait.isDone()) test &= true;
        		else test &= false;
        	}
        	Thread.sleep(100);
        }
        for (String name : fileNames){
        	File file = new File("data/pendings/" + name);
        	file.delete();
        }
        
        return "success";
    }
}