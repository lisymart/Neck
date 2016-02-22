package neck.neck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.springframework.web.servlet.ModelAndView;


@Controller
@SpringBootApplication
@EnableAsync
public class PcapController {    
	@Autowired
	private LogstashProcessService lps;
	
	private Map<String, String[]> requestMap;
	private TreeSet<String> params = new TreeSet<>();;
    private TreeSet<String> rename = new TreeSet<>();;
    private TreeSet<String> fileNames = new TreeSet<>();;
    private List<Future<String>> results ;
	
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="uploadToES")
    	public String uploadToES(HttpServletRequest request,@RequestParam final String addition) 
    			throws ServletException, IOException, InterruptedException {
    	TreeMap<String, String> renaming = new TreeMap<String, String>();
        for (String s : rename){
        	renaming.put(s, request.getParameter(s));
        }
        results = new ArrayList<>();
        
        for (String name : fileNames){
        	File folder = new File("data/pendings/" + name);
        	for (File fileName : folder.listFiles()){        
        		results.add(lps.process(fileName, renaming, addition));
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
        	File folder = new File("data/pendings/" + name);
        	for (File fileName : folder.listFiles()){        
        		File file = new File (fileName.getAbsolutePath());
        		file.delete();
        	}
        	File file = new File("data/pendings/" + name);
        	file.delete();
        }
        
        return "success";
    }
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="rename")
	public ModelAndView rename(HttpServletRequest request){
    	if (params.isEmpty()) {
    		requestMap = request.getParameterMap();
    		init();
    	}
        String[] checked = request.getParameterValues("checked");
        for (String att : checked){
        	rename.add(att);
        }
        params.removeAll(Arrays.asList(checked));
        Map<String, Object> model = new HashMap<>();
        model.put("fileNames", fileNames);
        model.put("renameList", rename);
        model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
     
    public void init(){
    	for (String s: requestMap.get("attributes")){
    		params.add(s);
    	}
    	fileNames();
    }
    
    public void fileNames(){
    	for (String attName : requestMap.keySet()){
        	if (attName.endsWith(".pcap") || attName.endsWith(".csv") || attName.endsWith(".log")){
        		fileNames.add(attName);
        	}
        }
    }
}