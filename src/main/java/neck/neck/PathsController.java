package neck.neck;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;

@Service
public class PathsController {

	private Logger logger = LoggerFactory.getLogger(PathsController.class);
	
	
	/*
	 * Method checkInstallations creates scripts with installation of systems check that are executed.
	 * @return	Collection of names of systems that are properly set.
	 */
    public List<String> checkInstallations() throws InterruptedException, TimeoutException, IOException {  
    	logger.info("Checking the necessary installations.");
    	List<String> running = new ArrayList<String>();
    	
    	//Checking whether Bro system is properly installed and set.
    	try{
    		new ProcessExecutor().command("broctl", "status").execute();
    		running.add("bro");
    	} catch (IOException ex){
    		logger.error("Environmental variables for Bro are not set!");
    	}
        
        //Checking whether Logstash system is properly installed and set and Installing missing plugin.
    	try {
    		new ProcessExecutor().command("logstash", "--version").execute();
    		running.add("logstash");
    		new ProcessExecutor().command("plugin", "install", "logstash-filter-range");
    	} catch (IOException ex) {
    		logger.error("Environmental variables for Logstash are not set!");
    	}
    	
    	//


        
        //Creating configuration file for Bro that converts the format of timestamp from Unix to ISO8601.
        PrintWriter writer = new PrintWriter("json_iso8601.bro");
        writer.println("@load policy/tuning/json-logs");
        writer.println("redef LogAscii::json_timestamps = JSON::TS_ISO8601;");
        writer.close();
        
        return running; 
    }
}
