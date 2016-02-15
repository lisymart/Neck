package neck.neck;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    
    BroProcessService bps = new BroProcessService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    @ResponseBody    
    @RequestMapping(value = "/loadFile", method = RequestMethod.POST)
    public ModelAndView loadFile(@RequestParam String nameOfFile, @RequestParam MultipartFile fileToUpload) throws IOException, URISyntaxException, ServletException{
    	String fileName = nameOfFile;
    	if (fileName.equals("")) fileName = fileToUpload.getOriginalFilename();
    	
        /*Configuration conf = new Configuration();
        FileSystem hdfs = FileSystem.get( new URI( "hdfs://localhost" ), conf );
        Path path = new Path("hdfs://localhost/"+fileName);
        if ( hdfs.exists( path )) return new ModelAndView("loadFIle", "message", "File with given name alredy exists. Please rename.");*/
        
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
    	stream.write(fileToUpload.getBytes());
    	stream.close();
        
        
        File file = new File(fileName);
        
        //hdfs.copyFromLocalFile(new Path(file.getAbsolutePath()), path);
        
        if(file.exists() && !file.isDirectory()){
            Files.write(Paths.get("paths.txt"), (file.getAbsolutePath() + "\n").getBytes(), StandardOpenOption.APPEND);
        
        String[] filetype = fileName.split("\\.");               
        switch (filetype[1]) {
            case "pcap" : 
                System.out.println("pcap"); 
                bps.broProcess();
                return new ModelAndView("pcap", "attributesList", showAttributes());
            case "csv" :
                System.out.println("csv");
                return new ModelAndView("csv");
            case "log" :
                System.out.println("log");
                return new ModelAndView("log");
    }
        } else {
            return new ModelAndView("loadFile", "message", "Path to file is incorrect. Try again.");
        }
        return new ModelAndView("loadFile");
    }   
    
    

    public TreeSet<String> showAttributes() throws ServletException, IOException {
        final Date date = bps.getDate();
        TreeSet<String> attributes = new TreeSet<String>();
        File folder = new File(dateFormat.format(date)); 
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
        return attributes;        
    }
}