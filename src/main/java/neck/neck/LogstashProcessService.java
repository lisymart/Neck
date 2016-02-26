package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Service
public class LogstashProcessService{

    @Async
    public Future<String> process(File fileName, String configuration) throws IOException, InterruptedException{
        String renaming = fileName.getAbsolutePath().replace("/", "-");
        if (fileName.getAbsolutePath().endsWith(".log") || fileName.getAbsolutePath().endsWith(".csv")){   
        	System.out.println("[" + Thread.currentThread().getName() + "] - " + fileName.getAbsolutePath() + " is being logstashed.");
        	PrintWriter writer = new PrintWriter("script" + renaming + ".sh", "UTF-8");    
        	writer.println("#!/bin/sh ");
        	writer.println("logstash agent -f " +configuration+ " < " + fileName.getAbsolutePath());
        	writer.close();
        	ProcessBuilder pb = new ProcessBuilder("/bin/bash", "script" + renaming + ".sh");
        	Process p = pb.start();           
        	
        	try {   
        		p.waitFor();
        	} catch (InterruptedException ex) {
        		java.util.logging.Logger.getLogger(ShowOptionsController.class.getName()).log(Level.SEVERE, null, ex);
        	}
        	File file = new File("script" + renaming + ".sh");
        	file.delete();
        }   
        System.out.println("[" + Thread.currentThread().getName() + "] - " + fileName.getAbsolutePath() + " ~ done.");
        return new AsyncResult<String>("progress");
    }
}
