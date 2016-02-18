package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Service
public class LogstashProcessService{
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");

    @Async
    public Future<String> process(File fileName, TreeMap<String, String> attributes, String addition) throws IOException, InterruptedException{
        LogConfig conf = new LogConfig(attributes, addition);
        String configuration = conf.getConfig().getAbsolutePath();
        String renaming = fileName.getAbsolutePath().replace("/", "-");
        if (fileName.getAbsolutePath().endsWith(".log")){   
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
        		java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        	}
        	File file = new File("script" + renaming + ".sh");
        	file.delete();
        }   
        System.out.println("[" + Thread.currentThread().getName() + "] - " + fileName.getAbsolutePath() + " ~ done.");
        return new AsyncResult<String>("progress");
    }
}
