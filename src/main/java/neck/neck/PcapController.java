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
	
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="uploadToES")
    	public String uploadToES(HttpServletRequest request,@RequestParam final String addition) 
    			throws ServletException, IOException, InterruptedException {
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	List<Future<String>> results = new ArrayList<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        LogConfig confFile= new LogConfig(ts, renaming, delete, uppercase, lowercase, anonymize, annmAlgo, addition);
        
        for (String name : fileNames){
        	File folder = new File("data/pendings/" + name);
        	for (File fileName : folder.listFiles()){        
        		results.add(lps.process(fileName, confFile.getConfig().getAbsolutePath()));
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
     
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="rnm")
	public ModelAndView rename(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        for (String att : checked){
        	rename.add(att);
        }
        params.removeAll(rename);
        Map<String, Object> model = new HashMap<>();
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="dlt")
	public ModelAndView delete(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        for (String att : checked){
        	delete.add(att);
        }
        params.removeAll(delete);
        Map<String, Object> model = new HashMap<>();
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="ts")
	public ModelAndView timeStamp(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        if (checked.length > 1) {
        	message = "Only one field can represent the Time Stamp";
        } else {
        	timeStamp = checked[0];
        	params.remove(timeStamp);
        }
        Map<String, Object> model = new HashMap<>();
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
    	if (message != null) model.put("message", message);
    	if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="uc")
	public ModelAndView uppercase(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        for (String att : checked){
        	uppercase.add(att);
        }
        params.removeAll(uppercase);
        Map<String, Object> model = new HashMap<>();
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="annm")
	public ModelAndView anonymize(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        for (String att : checked){
        	anonymize.add(att);
        }
        params.removeAll(anonymize);
        Map<String, Object> model = new HashMap<>();
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST, params="lc")
	public ModelAndView lowercase(HttpServletRequest request){
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
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
        for (String att : checked){
        	lowercase.add(att);
        }
        params.removeAll(lowercase);
        Map<String, Object> model = new HashMap<>();
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", rename);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("pcap", model);
    }
}