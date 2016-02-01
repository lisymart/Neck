package neck.neck;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class PcapController {    
       
    @Autowired
    LogstashProcessService lps;
    
    @Autowired
    BroProcessService bps;
    
    private HashMap<String, ArrayList<String>> attributes;
    private HashMap<String, HashMap<String, String>> change = new HashMap<String, HashMap<String, String>>();
    
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    public Callable<String> pcap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(Thread.currentThread().getName());
        System.out.println(attributes);
        for (String fileName : attributes.keySet()){
        	HashMap<String, String> temp = new HashMap<String, String>();
        	for(String attName : attributes.get(fileName)){
        	temp.put(attName, request.getParameter(fileName + attName));
        	}
        	change.put(fileName, temp);
        	System.out.println(temp);
        }
        System.out.println(change);
        final Date date = bps.getDate();
        Callable<String> asyncTask = new Callable<String>() {
 
            @Override
            public String call() throws Exception {
                return lps.process(date, change);
      }
    };
        return asyncTask;        
    }
    
    
    @RequestMapping(value = "/pcap", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Date date = bps.getDate();
        attributes = new HashMap <String, ArrayList<String>>();
        File folder = new File(dateFormat.format(date)); 
        List<File> list = Arrays.asList(folder.listFiles());
        for (File f : list) {
        	String line = Files.newBufferedReader(f.toPath(), Charset.forName("ISO-8859-1")).readLine();
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
        	attributes.put(f.getName(), names2);
        }
        System.out.println(attributes);
        return new ModelAndView("pcap", "attributesList", attributes);        
    }
}