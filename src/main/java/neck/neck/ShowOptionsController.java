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

/*
 * Class ShowOptionsController shows the supported transformations over the uploaded files.
 * @author Martin Lisý 
 */
@Controller
@SpringBootApplication
@EnableAsync
public class ShowOptionsController {    
	@Autowired
	private LogstashProcessService lps;
	
	private DateFormat hourFormat = new SimpleDateFormat("HH-mm-ssss");
    
	/*
	 * Method showOptions defines what transformations and over which attributes are available.
	 * @param	request			HttpServletRequest from .jsp page.
	 * @param	restoreParam	Represents the button Restore.
	 * @param	rnmParam		Represents the button Rename.
	 * @param	rngParam		Represents the button Range.
	 * @param	tsParam			Represents the button Timestamp.
	 * @param	ucParam			Represents the button Uppercase.
	 * @param	lcParam			Represents the button Lowercase.
	 * @param	annmParam		Represents the button Anonymize.
	 * @param	dltParam		Represents the button Delete.
	 * @param	addNewField		Represents the button Add new field.
	 * @param	uploadParam		Represents the button Upload to ES.
	 * @param	addition		Code with specified transformation that are not supported by default in Neck.
	 * @return	ModelAndView object within the new .jsp page.
	 */
    @RequestMapping(value = "/showOptions", method = RequestMethod.POST)
	public ModelAndView showOptions(HttpServletRequest request, @RequestParam(value="restore", required=false) String restoreParam, 
            												@RequestParam(value="rnm", required=false) String rnmParam,
            												@RequestParam(value="rng", required=false) String rngParam,
															@RequestParam(value="ts", required=false) String tsParam,
															@RequestParam(value="uc", required=false) String ucParam,
															@RequestParam(value="lc", required=false) String lcParam,
															@RequestParam(value="annm", required=false) String annmParam,
    														@RequestParam(value="dlt", required=false) String dltParam,
    														@RequestParam(value="addn", required=false) String addNewField,
    														@RequestParam(value="uploadToES", required=false) String uploadParam,
    														@RequestParam final String addition) 
    														throws IOException, InterruptedException{
    	//Collections with attributes of specified transformations.
    	TreeSet<String> fileNames = new TreeSet<>(Arrays.asList(request.getParameterValues("fileNames")));
    	TreeSet<String> params = new TreeSet<>();
        TreeSet<String> rename = new TreeSet<>();
        TreeMap<String, String> renaming = new TreeMap<>();
        TreeSet<String> range = new TreeSet<>();
    	TreeMap<String, ArrayList<String>> ranging = new TreeMap<>();
        TreeSet<String> delete = new TreeSet<>();
        TreeSet<String> uppercase = new TreeSet<>();
        TreeSet<String> lowercase = new TreeSet<>();
        TreeSet<String> anonymize = new TreeSet<>();
        TreeMap<String, String> newFields = new TreeMap<>();
        List<Future<String>> results = new ArrayList<>();
    	String[] checked = request.getParameterValues("checked");
        String store = request.getParameter("store");
        String stored = request.getParameter("stored");
        String ES = request.getParameter("ES");
        String timeStamp = null;
        String message = null;
        String hashingKey = null;
        String tsFormat = null;
        
        //Checking which attributes have been specified in previous step.
        if (null != request.getParameterValues("rename")) {
        	rename = new TreeSet<>(Arrays.asList(request.getParameterValues("rename")));
        	for (String s: rename){
        		renaming.put(s, request.getParameter("rnm" + s));
        	}
        }
        if (null != request.getParameterValues("range")) {
        	range = new TreeSet<>(Arrays.asList(request.getParameterValues("range")));
        	for (String s: range){
        		ArrayList<String> tmp = new ArrayList<>();
        		tmp.addAll(Arrays.asList(request.getParameterValues("rng" + s)));
        		ranging.put(s, tmp);
        	}
        }
        
        if (null != request.getParameterValues("fn")) {
        	ArrayList<String> newAttNames = new ArrayList<>(Arrays.asList(request.getParameterValues("fn")));
        	for (int i=0;i < newAttNames.size();i++){
        		newFields.put(request.getParameter("fn" + newAttNames.get(i)), request.getParameter("fv" + newAttNames.get(i)));
        	}
        }
        
        if (null != request.getParameterValues("delete")) {
        	delete = new TreeSet<>(Arrays.asList(request.getParameterValues("delete")));
        }
        if (null != request.getParameterValues("params")) {
        	params.addAll(Arrays.asList(request.getParameterValues("params")));
        }
        if (null != request.getParameterValues("timeStamp")) {
        	timeStamp = request.getParameterValues("timeStamp")[0];
       		tsFormat = request.getParameter("timeStampFormat");
        }
        if (null != request.getParameterValues("uppercase")) {
        	uppercase = new TreeSet<>(Arrays.asList(request.getParameterValues("uppercase")));
        }
        if (null != request.getParameterValues("lowercase")) {
        	lowercase = new TreeSet<>(Arrays.asList(request.getParameterValues("lowercase")));
        }
        if (null != request.getParameterValues("anonym")) {
        	anonymize = new TreeSet<>(Arrays.asList(request.getParameterValues("anonym")));
       		hashingKey = request.getParameter("hashingKey");
        }
        
        //Actions performed when Restore button is pressed. All transformations are reset.
        if (restoreParam != null) {
            if (timeStamp!= null) params.add(timeStamp);
            params.addAll(delete);
            anonymize.clear();
            uppercase.clear();
            lowercase.clear();
            timeStamp = null;
            delete.clear();
            rename.clear();
            range.clear();
            newFields.clear();
        }
        
        //Actions performed when Rename button is pressed.
        if (rnmParam != null) {
            if (null != checked){
            	for (String att : checked){
            		rename.add(att);
            	}
            	for (String s : rename){
                	renaming.put(s, request.getParameter("rnm" + s));
                }
            }
        }
        
        //Actions performed when Range button is pressed.
        if (rngParam != null) {
            if (null != checked){
            	for (String att : checked){
            		range.add(att);
            	}
                for (String s : range){
               		ArrayList<String> rng = new ArrayList<>();
               		if (null != request.getParameterValues("rng" + s))
               		rng.addAll(Arrays.asList(request.getParameterValues("rng" + s)));
               		ranging.put(s, rng);
                }
            }
        }
        //Actions performed when Delete button is pressed.        
        if (dltParam != null) {
            if (null != checked){
            	for (String att : checked){
            		delete.add(att);
            	}
            }
            if (checked != null) params.removeAll(Arrays.asList(checked));
        }

        //Actions performed when Timestamp button is pressed.        
        if (tsParam != null) {
            if (null != checked && checked.length > 1) {
            	message = "Only one field can represent the Time Stamp";
            } 
            if (null != checked && checked.length == 1) {
            	timeStamp = checked[0];
            	params.remove(timeStamp);
            }
            if (null == request.getParameter("tsFormat")) tsFormat = "ISO8601";
            if (checked != null) params.removeAll(Arrays.asList(checked));
        }
        
        //Actions performed when Uppercase button is pressed.        
        if (ucParam != null) {
            if (null != checked){
            	for (String att : checked){
            		uppercase.add(att);
            	}
            }
        }
        
        //Actions performed when Anonymize button is pressed.        
        if (annmParam != null){
            if (null != checked){
            	for (String att : checked){
            		anonymize.add(att);
            	}
            	if (null == request.getParameter("hashingKey")) hashingKey = "HashingKey";
            }
        }
        
        //Actions performed when Lowercase button is pressed.        
        if (lcParam != null) {
        	if (null != checked){
            	for (String att : checked){
            		lowercase.add(att);
            	}
            }
        }
        
        //Actions performed when Add new field button is pressed.        
        if(addNewField != null){
        	if (newFields.isEmpty()){
        		newFields.put("1", "field value");
        	} else {
        		int number = newFields.keySet().size() + 1;
        		newFields.put(String.valueOf(number), "field value");
        	}
        }
        
        //Actions performed when Upload to ES button is pressed.        
        if (uploadParam != null) {
            String ts = timeStamp + "->" + request.getParameter("timeStampFormat");
            String annmAlgo = request.getParameter("annmAlgo");
            TreeMap<String, double[]> rngs = new TreeMap<>();
            Date date = new Date();
            
            //Creating the configuration file with selected transformations over uploaded files.
            LogConfig confFile= new LogConfig(ES, hourFormat.format(date) + ".conf", ts, renaming, ranging, delete, uppercase, lowercase, anonymize, hashingKey, annmAlgo, newFields, addition);
            String configPath = confFile.getConfig(hourFormat.format(date) + ".conf").getAbsolutePath();
            
            //If stored files are being processed.
            if (stored.contains("stored")){
            	for (String name : fileNames){
                	File folder = new File("data/stored/" + name);
                	for (File fileName : folder.listFiles()){        
                		results.add(lps.process(fileName, configPath));
                	}
                }
            }
            
            //If new uploaded .pcap files are being processed.
            if (stored.contains("new")){
            	for (String name : fileNames){
                	File folder = new File("data/pendings/" + name);
                	for (File fileName : folder.listFiles()){        
                		results.add(lps.process(fileName, configPath));
                	}
                }
            }
            //If new uploaded log files are being processed.
            if (stored.contains("single")){
            	for (String name : fileNames){
            		File file = new File("data/uploads/" + name);
            		results.add(lps.process(file, configPath));
            	}
            }
            
            //Checking whether the asynchronous uploading of logs to Elasticsearch cluster is finished.
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
            
            //Storing processed .pcap files
            if (store.contains("store") && stored.contains("new")) {
        		for (String name : fileNames){
                	File srcDir = new File("data/pendings/"+name);
                	File destDir = new File("data/stored/"+name);
            	FileUtils.moveDirectory(srcDir, destDir);
        		}
            }
            
            //Storing processed .log (.txt, .json) files.
            if (store.contains("store") && stored.contains("single")) {
        		for (String name : fileNames){
                	File srcDir = new File("data/uploads/"+name);
                	File destDir = new File("data/stored/"+name+"/"+name);
            	FileUtils.moveFile(srcDir, destDir);
        		}
            } 
            
            //Deleting processed .pcap files if storing was not selected.
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
            
            //Deleting processed .log (.txt, .json) files if storing was not selected.
            if(store.contains("delete") && stored.contains("single")){
            	for (String nm : fileNames){
                   	File file = new File("data/uploads/" + nm);
                   	file.delete();
                }
            }
            
            //Deleting used Logstash configuration file.
            File cfg = new File(hourFormat.format(date) + ".conf");
            cfg.delete();
            
            return new ModelAndView("success", "ES", ES);
        }
        
        //Creating ModelAndView with selected transformations to visualize the selected changes. 
        params.removeAll(Arrays.asList("@version", "host", "message", "path"));
        Map<String, Object> model = new HashMap<>();
        model.put("store", store);
        model.put("stored", stored);
        model.put("ES", ES);
        model.put("hashingKey", hashingKey);
        model.put("tsFormat", tsFormat);
        if (!newFields.isEmpty()) model.put("addFieldList", newFields);
        if (message != null) model.put("message", message);
        if (!anonymize.isEmpty()) model.put("anonymList", anonymize);
        if (!uppercase.isEmpty()) model.put("uppercaseList", uppercase);
        if (!lowercase.isEmpty()) model.put("lowercaseList", lowercase);
        if (timeStamp != null) model.put("timeStamp", timeStamp);
        if (!delete.isEmpty()) model.put("deleteList", delete);
        if (!fileNames.isEmpty()) model.put("fileNames", fileNames);
        if (!rename.isEmpty()) model.put("renameList", renaming);
        if (!range.isEmpty()) model.put("rangeList", ranging);
        if (!params.isEmpty())model.put("attributesList", params);
        return new ModelAndView("showOptions", model);
    }
}