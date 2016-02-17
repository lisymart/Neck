package neck.neck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    public String process(HttpServletRequest request,@RequestParam final String addition) throws ServletException, IOException, InterruptedException {
        Map<String, String[]> params = request.getParameterMap();
        final TreeMap<String, String> change = new TreeMap<String, String>();

        for (String attName : params.keySet()){
        	change.put(attName, params.get(attName)[0]);
        }
        change.remove("addition");
        
        List<Future<String>> results = new ArrayList<>();
        File folder = new File("data/pendings");
        for (File folderName : folder.listFiles()){
        	for (File fileName : new File(folderName.getAbsolutePath()).listFiles()){        
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
        for (File folderName : folder.listFiles()){
        	File file = new File(folderName.getAbsolutePath());
        	file.delete();
        }
        
        return "success";
    }
}