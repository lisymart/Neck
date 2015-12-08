package neck.neck;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PcapController {
    
    @Autowired
    AsyncFileProcess afp;
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    public Callable<String> pcap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(Thread.currentThread().getName());
        Callable<String> asyncTask = new Callable<String>() {
 
            @Override
            public String call() throws Exception {
                return afp.process();
      }
    };
        return asyncTask;
        //response.sendRedirect("success.jsp");
    }
    
    @ResponseBody
    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    public ModelAndView progress(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        
        String progress = "In progress...";
        if (afp.getProgress() == 0) {progress =  "File is being processed by Bro network controller...";}
        if (afp.getProgress() > 0 ) {progress =  "File is being Logstashed. [" + afp.getProgress() + " / " + afp.getNumberOfFiles() + "] -- " +  afp.getProcessedFile();}
        if (afp.getProgress() < 0 ) {progress =  "Your file is not being processed. There must have an error occured.";}
        if (afp.getProgress() > afp.getNumberOfFiles()) {progress =  "Done";}
        return new ModelAndView("progress", "progress", progress);
    }
}
