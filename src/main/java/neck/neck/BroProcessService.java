package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.InvalidExitValueException;

@Service
public class BroProcessService {

	private Logger logger = LoggerFactory.getLogger(LogstashProcessService.class);
	private String dirPath = System.getProperty("user.dir");
	
	/*
     * Asynchronous method for processing .pcap files with Bro.
     * @param filePath		Absolute path to .pcap file.
     * @return	Future object of asynchronously processed files.
     */
    @Async   
    public Future<String> broProcess(String filePath) throws IOException, InvalidExitValueException, InterruptedException, TimeoutException, ExecutionException{
    	logger.info(filePath + " is being processed by Bro.");
        
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
}
