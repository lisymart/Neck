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

@Controller
public class IndexController {
    private static File paths = new File("paths.txt");

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public String index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String message = "";
    	String line = null;
        try{
            URL url = new URL("http://localhost:9200/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            line = bufferedReader.readLine();
            bufferedReader.close();
        } catch (ConnectException e) {            
        }        
    	if (line == null) message += "Elasticsearch is not running. Start ES and try again.<br>";
       
        if (paths.exists()) { if (line != null) return "loadFile"; else {request.setAttribute("message", message); return "index";}}
       	else {
       		
    	PathsController pc = new PathsController();
    	ArrayList<String> check =  pc.checkInstallations();
        if (check.size() < 3){
        	if (!check.contains("bro")) message += "Bro is not installed or properly set.<br>";
        	if (!check.contains("logstash")) message += "Logstash is not installed or properly set.<br>";
        	if (!check.contains("hadoop")) message += "Hadoop is not installed or properly set.<br>";
        	message += "Check your .bashrc for path variables."; 
        }       
        if (! message.equals("")) {
        	request.setAttribute("message",	message);
        	return "index";
        } else {    
        	paths.createNewFile();
        	return "loadFile";
        }
    }
    }
}


