package neck.neck;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PcapController {    
	@Autowired
	private BroProcessService bps;
	@Autowired
	private LogstashProcessService lps;
    
   
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    public String process(HttpServletRequest request,@RequestParam final String addition) throws ServletException, IOException, InterruptedException {
        final Date date = bps.getDate();
        Map<String, String[]> params = request.getParameterMap();
        final TreeMap<String, String> change = new TreeMap<String, String>();

        for (String attName : params.keySet()){
        	change.put(attName, params.get(attName)[0]);
        }
        change.remove("addition");
        lps.process(date, change, addition);
        return "success";
    }
}