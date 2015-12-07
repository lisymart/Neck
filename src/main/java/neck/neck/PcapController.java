package neck.neck;

import java.io.IOException;
import java.util.concurrent.Callable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PcapController {
    
    @Autowired
    AsyncFileProcess afp;
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    public Callable<String> pcap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Callable<String> asyncTask = new Callable<String>() {
 
            @Override
            public String call() throws Exception {
            return afp.process();
      }
    };
        return asyncTask;
        //response.sendRedirect("success.jsp");
    }


}
