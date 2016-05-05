package neck.neck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.zeroturnaround.exec.InvalidExitValueException;

/*
 * Controller of index.jsp page. 
 * @author	Martin Lisý
 */

@Controller
public class IndexController {
	// If the file path.txt is created then it indicates that all necessary systems are properly installed and set. 
    private static File paths = new File("paths.txt");

    /*
     * Controller for index.jsp page when the button Begin is pressed.
     * @param	request			HttpServletRequest from .jsp page.
     * @return	ModelaAndView object within the new .jsp page.
     */
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public ModelAndView index(HttpServletRequest request) throws TimeoutException, InvalidExitValueException, IOException, InterruptedException, ServletException {
    	Logger logger = LoggerFactory.getLogger(IndexController.class);
    	logger.info("Checking the Elastic health.");;
    	String message = "";
    	String EShost = request.getParameter("EShost");
    	
    	// Checking if selected Elasticsearch cluster is alive
    	RestTemplate template = new RestTemplate();
        Map<String,String> elasticHealth = new HashMap<String, String>();
        try {
        	elasticHealth.putAll(template.getForObject("http://" + EShost + "/_cluster/health", Map.class));
        } catch (RestClientException ex) {
        	logger.error(ex.getLocalizedMessage());
        	logger.warn("Elasticsearch is not running. Start ES or check host and try again.");
        	message += "Elasticsearch is not running. Start ES or check host and try again.";
        	
        }
		if (!elasticHealth.isEmpty()) {
			if (elasticHealth.get("status").contains("red")) message += "Elasticsearch has red status. Start ES (or check host) and try again.<br>";
		}
       
    	// If the file paths.txt exists, only Elasticsearch cluster health is checked. (Makes it faster)
        if (paths.exists()) 
        	if (!elasticHealth.isEmpty() && !elasticHealth.get("status").contains("red")) {
        		logger.info("Elasticsearch health OK.");
        		return new ModelAndView("loadFile", "ES", EShost);
        	}  	else return new ModelAndView("index", "message", message);
        
       	else {
        // If paths.txt does not exist, checking of necessary installation is required.
    	ExternalProcessService service = new ExternalProcessService();
    	List<String> check =  new ArrayList<>();
    	check.addAll(service.checkInstallations());
        if (check.size() < 2){
        	if (!check.contains("bro")) message += "Bro is not installed or properly set.<br>";
        	if (!check.contains("logstash")) message += "Logstash is not installed or properly set.<br>";
        	message += "Check your .bashrc for path variables and restart terminals."; 
        	logger.warn(message);
        }       
        if (! message.equals("")) {
        	return new ModelAndView("index", "message", message);
        } else {    
        	paths.createNewFile();
        	return new ModelAndView("loadFile", "ES", EShost);
        }
    }
    }
}


