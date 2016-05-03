package neck.neck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * Class LogstashProcessService asynchronously processes log files. 
 * With created configuration file stores the data into Elasticsearch cluster.
 * @Author	Martin Lisý
*/

@Service
public class LogstashProcessService{
	
	/*
	 * @param	fileName		File that is going to be processed.
	 * @param	configuration	Config file with defined transformations over the input file.
	 * @return	Future object of asynchronously log processing. 
	 */
    @Async
    public Future<String> process(File fileName, String configuration) throws IOException, InterruptedException, ExecutionException, TimeoutException{
    	Logger logger = LoggerFactory.getLogger(LogstashProcessService.class);

        String renaming = fileName.getAbsolutePath().replace("/", "-");
        if (fileName.getAbsolutePath().endsWith(".log") || fileName.getAbsolutePath().endsWith(".csv")){   
        	// Creates script that processes log files with transformation config file
        	logger.info(fileName.getAbsolutePath() + " is being logstashed.");
        	/*
        	Future<ProcessResult> output = new ProcessExecutor().command("logstash", "-f", configuration + " < " + fileName.getAbsolutePath())
                    .readOutput(true).start()
                    .getFuture(); 
        	
        	 String line = output.get(60, TimeUnit.SECONDS).outputUTF8();
             logger.info(line);
             */
        	
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
        	
        } 
        logger.info(fileName.getAbsolutePath() + " ~ done.");
        return new AsyncResult<String>("done");
    }
}
