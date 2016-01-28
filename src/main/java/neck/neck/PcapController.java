package neck.neck;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import javax.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        //        response.sendRedirect("/jsp/progress.jsp");

        return asyncTask;        
    }
    
    /*
    @RequestMapping(value = "/pcap", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Date date = bps.getDate();
        List<String> list = new ArrayList();
        File folder = new File(dateFormat.format(date)); 
        //        response.sendRedirect("/jsp/progress.jsp");

        return new ModelAndView("pcap", "attributesList", list);        
    }*/
}
