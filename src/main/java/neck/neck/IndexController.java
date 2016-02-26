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

@Controller
public class IndexController {
    private static File paths = new File("paths.txt");

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String message = "";
    	String line = null;
    	String EShost = request.getParameter("EShost");
        try{
            URL url = new URL("http://"+ EShost +"/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            line = bufferedReader.readLine();
            bufferedReader.close();
        } catch (ConnectException e) {            
        }        
    	if (line == null) message += "Elasticsearch is not running. Start ES (or check host) and try again.<br>";
       
        if (paths.exists()) { if (line != null) return new ModelAndView("loadFile", "ES", EShost); else return new ModelAndView("index", "message", message);}
       	else {
       		
    	PathsController pc = new PathsController();
    	ArrayList<String> check =  pc.checkInstallations();
        if (check.size() < 2){
        	if (!check.contains("bro")) message += "Bro is not installed or properly set.<br>";
        	if (!check.contains("logstash")) message += "Logstash is not installed or properly set.<br>";
        	message += "Check your .bashrc for path variables."; 
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


