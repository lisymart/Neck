package neck.neck;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

/*
 * Class PathsController controls the necessary installations of required systems. (Bro and Logstash).
 * @author Martin Lisý
 */
public class PathsController {
	/*
	 * Method checkInstallations creates scripts with installation of systems check that are executed.
	 * @return	Collection of names of systems that are properly set.
	 */
    public List<String> checkInstallations() throws ServletException, IOException, InvalidExitValueException, InterruptedException, TimeoutException {  
    	Logger logger = LoggerFactory.getLogger(PathsController.class);
    	logger.info("Checking the necessary installations.");
    	List<String> running = new ArrayList<String>();
    	
    	//Checking whether Bro system is properly installed and set.
    	String broOutput = new ProcessExecutor().command("broctl", "status")
                .readOutput(true).execute()
                .outputUTF8();    
        
        //Checking whether Logstash system is properly installed and set.
    	String logstashOutput = new ProcessExecutor().command("logstash", "--version")
                .readOutput(true).execute()
                .outputUTF8();    
    	
    	//Installing missing plugin
    	new ProcessExecutor().command("plugin", "install", "logstash-filter-range")
                .readOutput(true).execute();
        
               
       	if (!broOutput.contains("command")){
        	running.add("bro");
        }

       	if (!logstashOutput.contains("command")){
        	running.add("logstash");
        }
        
        //Creating configuration file for Bro that converts the format of timestamp from Unix to ISO8601.
        PrintWriter writer = new PrintWriter("json_iso8601.bro");
        writer.println("@load policy/tuning/json-logs");
        writer.println("redef LogAscii::json_timestamps = JSON::TS_ISO8601;");
        writer.close();
                        
        return running; 
    }
}