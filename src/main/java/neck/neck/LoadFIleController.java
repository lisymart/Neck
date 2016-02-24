package neck.neck;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SpringBootApplication
@EnableAsync
public class LoadFIleController {
	@Autowired
    BroProcessService bps;

    @ResponseBody    
    @RequestMapping(value = "/loadFile", method = RequestMethod.POST, params="continue")
    public ModelAndView loadFile(@RequestParam MultipartFile[] filesToUpload, HttpServletRequest request) throws IOException, URISyntaxException, ServletException, InterruptedException{
    	System.out.println(Thread.currentThread().getName());
    	ArrayList<String> fileNames = new ArrayList<>();
    	Set<String> filetypes = new HashSet<>();
    	String store = request.getParameter("store");
    	String filetype = null;
    	boolean stored=false;

    	for (MultipartFile file: filesToUpload){
			String name = file.getOriginalFilename();
			filetype = name.substring(name.lastIndexOf(".") + 1);
			filetypes.add(filetype);
		}
    	
    	if (filetypes.size() > 1) return new ModelAndView("loadFile", "message", "You selected files with different types. Only one type is allowed.");
    	if (filetypes.toArray()[0] == "" && null == request.getParameterValues("checked")) return new ModelAndView("loadFile", "message", "No file selected to upload or no stored file selected to process.");
    	if (filetypes.toArray()[0] != "" && null != request.getParameterValues("checked")) return new ModelAndView("loadFile", "message", "You selected a file to upload and chosed stored file to process. Only one is allowed.");
    	TreeSet<String> checked = new TreeSet<>();
    	if (null != request.getParameterValues("checked")){
    		checked = new TreeSet<>(Arrays.asList(request.getParameterValues("checked")));
    		for (String s: checked){
    			fileNames.add(s);
    		}
    		for (String s: fileNames){
    			filetype = s.substring(s.lastIndexOf(".") + 1);
    			filetypes.add(filetype);
    		}
    		if (filetypes.size() > 1) return new ModelAndView("loadFile", "message", "You selected files with different types. Only one type is allowed.");
    		stored=true;
    	} else {
    		File uploads = new File("data/uploads");
    		if (!uploads.exists()) uploads.mkdirs();
    	
    		for (MultipartFile file : filesToUpload){
    			fileNames.add(file.getOriginalFilename());
    			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("data/uploads/" + file.getOriginalFilename())));
    			stream.write(file.getBytes());
    			stream.close();
    		}
    	}     
        System.out.println(filetype);
        switch (filetype) {
            case "pcap" : 
                if(!stored) {
                	List<Future<String>> results = new ArrayList<>();
                	for (String filePath : fileNames){
                		results.add(bps.broProcess(filePath));
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
                }
                TreeSet<String> attributesPcap;
                if (stored){
                	 attributesPcap = showAttributes(fileNames, "stored");
                } else {
                	attributesPcap = showAttributes(fileNames, "pendings");
                }
                
                if (!stored) {
                	for (String name : fileNames){
                		File file = new File ("data/uploads/" + name);
                		file.delete();
                	}
                }
                Map <String, Object> modelPcap = new HashMap<>();
                String save;
                if (null == store) {
                	save = "delete";
                } else {
                	save = "store";
                }
                if (stored){
                	modelPcap.put("stored", "stored");
                } else {
                	modelPcap.put("stored", "new");
                }
                modelPcap.put("store", save);
                modelPcap.put("fileNames", fileNames);
                modelPcap.put("attributesList", attributesPcap);
                
                return new ModelAndView("showOptions", modelPcap);
            case "csv": 
            case "log":
            	TreeSet<String> attributes;
            	if (stored) {
            		attributes = showAttributes(fileNames, "stored");
            	} else {
            		attributes = showAttributes(fileNames, "uploads");
            	}
                Map <String, Object> model = new HashMap<>();
                if (null == store) {
                	save = "delete";
                } else {
                	save = "store";
                }
                if (stored){
                	model.put("stored", "stored");
                } else {
                	model.put("stored", "single");
                }
                model.put("store", save);
                model.put("fileNames", fileNames);
                model.put("attributesList", attributes);
                
                return new ModelAndView("showOptions", model);
        }
        return new ModelAndView("loadFile", "message", "Unsupported file type.");
    }   
    
    @RequestMapping(value = "/loadFile", method = RequestMethod.POST, params="chooseFile")
    public ModelAndView chooseFile(HttpServletRequest request) {
    	File folder = new File("data/stored");
    	ArrayList<String> names = new ArrayList<>();
    	Map <String, Object> model = new HashMap<>();
    	if (null == folder.listFiles()){
    		model.put("message", "No files has been stored on server yet.");
    	} else {
    		for (File f: folder.listFiles()){
        		names.add(f.getName());
        	}
    		model.put("fileList", names);
    	}
    	
    	return new ModelAndView("loadFile", model);
    }

    public TreeSet<String> showAttributes(ArrayList<String> fileNames, String location) throws ServletException, IOException {
        TreeSet<String> attributes = new TreeSet<String>();
        for (String fileName : fileNames){
        	File folder = new File("data/" + location + "/" + fileName); 
        	if (folder.isDirectory()){
        		for (File f : folder.listFiles()) {
        		getAtts(attributes, f);
        		}
        	} else {
        		getAtts(attributes, folder);
        	}
        }
        return attributes;        
    }	
    
    public void getAtts(TreeSet<String> attributes, File f) throws IOException{
    	BufferedReader br = Files.newBufferedReader(f.toPath(), Charset.forName("ISO-8859-1")); 
		String line = br.readLine();   	
		int i = 0;
		while (line != null && i<=1000) {        		        		
			ArrayList<String> names = new ArrayList<>();
			List<String> temp = Arrays.asList(line.split("\":"));
			for (String s: temp){
				List<String> temp2 = Arrays.asList(s.split(","));
				names.add(temp2.get(temp2.size()-1));            	
			}
			names.remove(names.size()-1);
			ArrayList<String> names2 = new ArrayList<>();
			for (String s : names){
				names2.add(s.split("\"")[1]);
			}
			attributes.addAll(names2); 
			line = br.readLine();        		
			i++;
		}
    }
    
}