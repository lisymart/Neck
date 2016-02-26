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
import javax.servlet.ServletException;
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
	
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="uploadToES")
    	public String uploadToES(HttpServletRequest request,@RequestParam final String addition) 
    			throws ServletException, IOException, InterruptedException {
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	List<Future<String>> results = new ArrayList<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
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
    	
        TreeMap<String, String> renaming = new TreeMap<String, String>();
        for (String s : rename){
        	if (request.getParameter(s) != "")
        	renaming.put(s, request.getParameter(s));
        }
        String ts = timeStamp + "->" + request.getParameter("timeStampFormat");
        String annmAlgo = request.getParameter("annmAlgo");
        Date date = new Date();
        LogConfig confFile= new LogConfig(hourFormat.format(date) + ".conf", ts, renaming, delete, uppercase, lowercase, anonymize, annmAlgo, addition);
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
        
        return "success";
    }
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="restore")
	public ModelAndView restore(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
        
        params.addAll(anonymize);
        params.addAll(uppercase);
        params.addAll(lowercase);
        if (timeStamp!= null) params.add(timeStamp);
        params.addAll(delete);
        params.addAll(rename);
        
        Map<String, Object> model = new HashMap<>();
        model.put("stored", stored);
        model.put("store", store);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
     
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="rnm")
	public ModelAndView rename(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
    	
        String[] checked = request.getParameterValues("checked");
        if (null != checked){
        	for (String att : checked){
        		rename.add(att);
        	}
        }
        
        params.removeAll(rename);
        Map<String, Object> model = new HashMap<>();
        model.put("store", store);
        model.put("stored", stored);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="dlt")
	public ModelAndView delete(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
        
        String[] checked = request.getParameterValues("checked");
        if (null != checked){
        	for (String att : checked){
        		delete.add(att);
        	}
        }
        
        params.removeAll(delete);
        Map<String, Object> model = new HashMap<>();
        model.put("store", store);
        model.put("stored", stored);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="ts")
	public ModelAndView timeStamp(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        String message = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
        

        String[] checked = request.getParameterValues("checked");
        if (null != checked && checked.length > 1) {
        	message = "Only one field can represent the Time Stamp";
        } 
        if (null != checked && checked.length == 1) {
        	timeStamp = checked[0];
        	params.remove(timeStamp);
        }
        
        Map<String, Object> model = new HashMap<>();
        model.put("store", store);
        model.put("stored", stored);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
    	if (message != null) model.put("message", message);
    	if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="uc")
	public ModelAndView uppercase(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
        
        String[] checked = request.getParameterValues("checked");
        if (null != checked){
        	for (String att : checked){
        		uppercase.add(att);
        	}
        }
        
        params.removeAll(uppercase);
        Map<String, Object> model = new HashMap<>();
        model.put("store", store);
        model.put("stored", stored);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="annm")
	public ModelAndView anonymize(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
        
        String[] checked = request.getParameterValues("checked");
        if (null != checked){
        	for (String att : checked){
        		anonymize.add(att);
        	}
        }
        params.removeAll(anonymize);
        Map<String, Object> model = new HashMap<>();
        model.put("stored", stored);
        model.put("store", store);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
    
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST, params="lc")
	public ModelAndView lowercase(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String timeStamp = null;
        
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        }
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params = new TreeSet<>(Arrays.asList(request.getParameterValues("params")));
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
        
        String[] checked = request.getParameterValues("checked");
        if (null != checked){
        	for (String att : checked){
        		lowercase.add(att);
        	}
        }
        
        params.removeAll(lowercase);
        Map<String, Object> model = new HashMap<>();
        model.put("stored", stored);
        model.put("store", store);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
}