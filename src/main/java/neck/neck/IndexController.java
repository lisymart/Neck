package neck.neck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView index(HttpServletRequest request) throws ServletException, IOException {
    	String message = "";
    	String line = null;
    	String EShost = request.getParameter("EShost");
    	
    	// Checking if selected Elasticsearch cluster is alive
        try{
            URL url = new URL("http://"+ EShost +"/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            line = bufferedReader.readLine();
            bufferedReader.close();
        } catch (ConnectException e) {            
        }        
    	if (line == null) message += "Elasticsearch is not running. Start ES (or check host) and try again.<br>";
       
    	// If the file paths.txt exists, only Elasticsearch cluster health is checked. (Makes it faster)
        if (paths.exists()) { if (line != null) return new ModelAndView("loadFile", "ES", EShost); else return new ModelAndView("index", "message", message);}
       	else {
       	
        // If paths.txt does not exist, checking of necessary installation is required.
    	PathsController pc = new PathsController();
    	ArrayList<String> check =  pc.checkInstallations();
        if (check.size() < 2){
        	if (!check.contains("bro")) message += "Bro is not installed or properly set.<br>";
        	if (!check.contains("logstash")) message += "Logstash is not installed or properly set.<br>";
        	message += "Check your .bashrc for path variables and restart terminals."; 
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


