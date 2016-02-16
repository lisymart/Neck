package neck.neck;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoadFIleController {
    private ArrayList<String> fileNames = new ArrayList<>();

    @ResponseBody    
    @RequestMapping(value = "/loadFile", method = RequestMethod.POST)
    public ModelAndView loadFile(@RequestParam MultipartFile[] filesToUpload) throws IOException, URISyntaxException, ServletException{
    	Set<String> filetypes = new HashSet<>();
    	String filetype = null;;
    	for (MultipartFile file: filesToUpload){
    		String name = file.getOriginalFilename();
    		String[] type = name.split("\\.");
    		filetype = type[type.length-1];
    		filetypes.add(filetype);
    	}
    	if (filetypes.size() > 1) return new ModelAndView("loadFile", "message", "You selected files with different types. Only one type is allowed.");
    	
    	File uploads = new File("data/uploads");
    	if (!uploads.exists()) uploads.mkdirs();
    	
    	for (MultipartFile file : filesToUpload){
    		fileNames.add(file.getOriginalFilename());
    		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("data/uploads/" + file.getOriginalFilename())));
        	stream.write(file.getBytes());
        	stream.close();
    	}
        
        for (String fileName : fileNames){
        	File file = new File ("data/uploads/" + fileName);
        if(file.exists() && !file.isDirectory())
            Files.write(Paths.get("paths.txt"), (file.getAbsolutePath() + "\n").getBytes(), StandardOpenOption.APPEND);
        }
              
        
        switch (filetype) {
            case "pcap" : 
                System.out.println("pcap"); 
                for (String filePath : fileNames){
                    BroProcessService bps = new BroProcessService();
                    bps.broProcess(filePath);
                }
                return new ModelAndView("pcap", "attributesList", showAttributes());
            case "csv" :
                System.out.println("csv");
                return new ModelAndView("csv");
        }
        return new ModelAndView("loadFile", "message", "Something went wrong, please try again.");
    }   
    
    

    public TreeSet<String> showAttributes() throws ServletException, IOException {
        TreeSet<String> attributes = new TreeSet<String>();
        for (String fileName : fileNames){
        	File folder = new File("data/pendings/" + fileName); 
        	List<File> list = Arrays.asList(folder.listFiles());        
        	for (File f : list) {
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
        return attributes;        
    }
}