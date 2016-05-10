package neck.neck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class LogstashProcessService {

private Logger logger = LoggerFactory.getLogger(LogstashProcessService.class);
	
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
    
}
