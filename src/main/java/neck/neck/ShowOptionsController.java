package neck.neck;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
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
public class ShowOptionsController {    
	@Autowired
	private LogstashProcessService lps;
	
	private DateFormat hourFormat = new SimpleDateFormat("HH-mm-ssss");
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST)
	public ModelAndView restore(HttpServletRequest request, @RequestParam(value="restore", required=false) String restoreParam, 
            												@RequestParam(value="rnm", required=false) String rnmParam,
            												@RequestParam(value="rng", required=false) String rngParam,
															@RequestParam(value="ts", required=false) String tsParam,
															@RequestParam(value="uc", required=false) String ucParam,
															@RequestParam(value="lc", required=false) String lcParam,
															@RequestParam(value="annm", required=false) String annmParam,
    														@RequestParam(value="dlt", required=false) String dltParam,
    														@RequestParam(value="uploadToES", required=false) String uploadParam,
    														@RequestParam final String addition) 
    														throws IOException, InterruptedException{
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> range = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        List<Future<String>> results = new ArrayList<>();
    	String[] checked = request.getParameterValues("checked");
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String ES = request.getParameter("ES");
        String timeStamp = null;
        String message = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("range")) {
        	range = new TreeSet<>(Arrays.asList(request.getParameterValues("range")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params.addAll(Arrays.asList(request.getParameterValues("params")));
        }
        if (null != request.getParameterValues("timeStamp")) {
        	timeStamp = request.getParameterValues("timeStamp")[0];
        }
        if (null != request.getParameterValues("uppercase")) {
        	uppercase = new TreeSet<>(Arrays.asList(request.getParameterValues("uppercase")));
        }
        if (null != request.getParameterValues("lowercase")) {
        	lowercase = new TreeSet<>(Arrays.asList(request.getParameterValues("lowercase")));
        }
        if (null != request.getParameterValues("anonym")) {
        	anonymize = new TreeSet<>(Arrays.asList(request.getParameterValues("anonym")));
        }
         
        if (restoreParam != null) {
        	params.addAll(anonymize);
            params.addAll(uppercase);
            params.addAll(lowercase);
            params.addAll(range);
            if (timeStamp!= null) params.add(timeStamp);
            params.addAll(delete);
            params.addAll(rename);
            anonymize.clear();
            uppercase.clear();
            lowercase.clear();
            timeStamp = null;
            delete.clear();
            rename.clear();
            range.clear();
        }
        
        if (rnmParam != null) {
            if (null != checked){
            	for (String att : checked){
            		rename.add(att);
            	}
            }
        }
        
        if (rngParam != null) {
            if (null != checked){
            	for (String att : checked){
            		range.add(att);
            	}
            }
        }
        
        if (dltParam != null) {
            if (null != checked){
            	for (String att : checked){
            		delete.add(att);
            	}
            }
        }
        
        if (tsParam != null) {
            if (null != checked && checked.length > 1) {
            	message = "Only one field can represent the Time Stamp";
            } 
            if (null != checked && checked.length == 1) {
            	timeStamp = checked[0];
            	params.remove(timeStamp);
            }
        }
        
        if (ucParam != null) {
            if (null != checked){
            	for (String att : checked){
            		uppercase.add(att);
            	}
            }
        }
        
        if (annmParam != null){
            if (null != checked){
            	for (String att : checked){
            		anonymize.add(att);
            	}
            }
        }
        
        if (lcParam != null) {
        	if (null != checked){
            	for (String att : checked){
            		lowercase.add(att);
            	}
            }
        }
        
        if (uploadParam != null) {
        	TreeMap<String, String> renaming = new TreeMap<String, String>();
            for (String s : rename){
            	if (request.getParameter(s) != "")
            	renaming.put(s, request.getParameter(s));
            }
            
            TreeMap<String, double[]> ranging = new TreeMap<>();
            for (String s : range){
            	if (request.getParameter(s + "from") != "" && request.getParameter(s + "to") != "") {
            		double[] rng = {Double.parseDouble(request.getParameter(s + "from")), Double.parseDouble(request.getParameter(s + "to"))};
            		ranging.put(s, rng);
            	}
            }
            
            String ts = timeStamp + "->" + request.getParameter("timeStampFormat");
            String annmAlgo = request.getParameter("annmAlgo");
            Date date = new Date();
            LogConfig confFile= new LogConfig(ES, hourFormat.format(date) + ".conf", ts, renaming, ranging, delete, uppercase, lowercase, anonymize, annmAlgo, addition);
            String configPath = confFile.getConfig(hourFormat.format(date) + ".conf").getAbsolutePath();
            
            if (stored.contains("stored")){
            	for (String name : fileNames){
                	File folder = new File("data/stored/" + name);
                	for (File fileName : folder.listFiles()){        
                		results.add(lps.process(fileName, configPath));
                	}
                }
            }
            if (stored.contains("new")){
            	for (String name : fileNames){
                	File folder = new File("data/pendings/" + name);
                	for (File fileName : folder.listFiles()){        
                		results.add(lps.process(fileName, configPath));
                	}
                }
            }
            if (stored.contains("single")){
            	for (String name : fileNames){
            		File file = new File("data/uploads/" + name);
            		results.add(lps.process(file, configPath));
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
            
            File strd = new File("data/stored");
            if (!strd.exists()) strd.mkdirs();
            
            if (store.contains("store") && stored.contains("new")) {
        		for (String name : fileNames){
                	File srcDir = new File("data/pendings/"+name);
                	File destDir = new File("data/stored/"+name);
            	FileUtils.moveDirectory(srcDir, destDir);
        		}
            }
            if (store.contains("store") && stored.contains("single")) {
        		for (String name : fileNames){
                	File srcDir = new File("data/uploads/"+name);
                	File destDir = new File("data/stored/"+name+"/"+name);
            	FileUtils.moveFile(srcDir, destDir);
        		}
            } 
            if(store.contains("delete") && stored.contains("new")){
            	for (String nm : fileNames){
                   	File folder = new File("data/pendings/" + nm);
                   	for (File fileName : folder.listFiles()){        
                   		File file = new File (fileName.getAbsolutePath());
                   		file.delete();
                   	}
                   	File file = new File("data/pendings/" + nm);
                   	file.delete();
                }
            }
            if(store.contains("delete") && stored.contains("single")){
            	for (String nm : fileNames){
                   	File file = new File("data/uploads/" + nm);
                   	file.delete();
                }
            }
            File cfg = new File(hourFormat.format(date) + ".conf");
            cfg.delete();
            
            return new ModelAndView("success", "ES", ES);
        }
        
        if (checked != null) params.removeAll(Arrays.asList(checked));
        Map<String, Object> model = new HashMap<>();
        model.put("store", store);
        model.put("stored", stored);
        model.put("ES", ES);
        if (message != null) model.put("message", message);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!range.isEmpty()) model.put("rangeList", range);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
}