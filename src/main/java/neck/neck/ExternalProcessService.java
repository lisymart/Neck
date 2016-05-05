package neck.neck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

@Service
public class ExternalProcessService {
	private Logger logger = LoggerFactory.getLogger(ExternalProcessService.class);
	private String dirPath = System.getProperty("user.dir");
	
	/*
	 * @param	fileName		File that is going to be processed.
	 * @param	configuration	Config file with defined transformations over the input file.
	 * @return	Future object of asynchronously log processing. 
	 */
    @Async
    public Future<String> logstashProcess(File fileName, String configuration) throws IOException, InterruptedException, ExecutionException, TimeoutException{
       	// Creates script that processes log files with transformation config file
        logger.info(fileName.getAbsolutePath() + " is being processed by Logstash.");
        
        String renaming = fileName.getAbsolutePath().replace("/", "-");
        PrintWriter writer = new PrintWriter("script" + renaming + ".sh", "UTF-8");    
     	writer.println("#!/bin/sh ");
     	writer.println("logstash agent -f " +configuration+ " < " + fileName.getAbsolutePath());
     	writer.close();
     	ProcessBuilder pb = new ProcessBuilder("/bin/bash", "script" + renaming + ".sh");
     	Process p = pb.start();  
     	
     	try {   
     		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
     		while (reader.readLine() != null) {}
     		p.waitFor();
     	} catch (InterruptedException ex) {
        		logger.error(ex.getLocalizedMessage());
     	}
     	File file = new File("script" + renaming + ".sh");
     	//deletes executed script
     	file.delete();
           
        
        logger.info(fileName.getAbsolutePath() + " ~ done.");
        return new AsyncResult<String>("done");
    }
    
    /*
     * Asynchronous method for processing .pcap files with Bro.
     * @param filePath		Absolute path to .pcap file.
     * @return	Future object of asynchronously processed files.
     */
    @Async   
    public Future<String> broProcess(String filePath) throws IOException, InvalidExitValueException, InterruptedException, TimeoutException, ExecutionException{
    	logger.info(filePath + " is being processed by Bro.");;
        
        // using file json_iso8601.bro to specify the format of timestampt of incomming .pcap file
    	File cfg = new File("json_iso8601.bro"); 
        File pendings = new File("data/pendings");
        if (!pendings.exists()) pendings.mkdirs();
        
        // creating script with path to input .pcap file that is later executed 
        PrintWriter writer = new PrintWriter("script" + filePath +".sh", "UTF-8");     
        writer.println("#!/bin/sh ");
        writer.println("cd data/pendings");
        writer.println("mkdir " + filePath);
        writer.println("cd " + filePath);
        writer.println("bro -r " + dirPath + "/data/uploads/" + filePath + " " + cfg.getAbsolutePath());
        writer.println("rm -r .state");
        writer.println("cd ..");
        writer.println("cd ..");
        writer.println("cd ..");
        writer.close();
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "script" + filePath +".sh");
        Process p = pb.start();           
            
        try {   
            p.waitFor();
        } catch (InterruptedException ex) {
        	logger.error(ex.getLocalizedMessage());
        }
        
        // deleting processed script
        File file = new File("script" + filePath +".sh");
        file.delete();
        return new AsyncResult<String>(filePath);
    }
    
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
