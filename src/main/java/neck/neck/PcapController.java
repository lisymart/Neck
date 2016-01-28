package neck.neck;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
    
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    public Callable<String> pcap() throws ServletException, IOException {
        System.out.println(Thread.currentThread().getName());
        final Date date = bps.getDate();
        Callable<String> asyncTask = new Callable<String>() {
 
            @Override
            public String call() throws Exception {
                return lps.process(date);
      }
    };
        return asyncTask;        
    }
    
    /*
    @RequestMapping(value = "/pcap", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Date date = bps.getDate();
        File folder = new File(dateFormat.format(date)); 
        ArrayList<String> names = new ArrayList<>();
        List<File> list = Arrays.asList(folder.listFiles());
        Map<String, ArrayList<String>> attributes = new HashMap <String, ArrayList<String>>();
        for (File f : list) {
        	String line = Files.newBufferedReader(f.toPath(), Charset.forName("ISO-8859-1")).readLine();
        	for (String name : line.split(",")){
        		names.add(name.split(":")[0]);
        	}
        	attributes.put(f.getName(), names);
        	names.clear();        	
        }
        return new ModelAndView("pcap", "attributesList", attributes);        
    }*/
}
;