package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.logging.Level;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class LogstashProcessService{
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");
    private Integer progress = -1;
    private Integer numberOfFiles = 0;
    private String processedFile;
    public int getProgress() {
        return progress;
    }

    public int getNumberOfFiles() {
        return numberOfFiles - 1;
    }

    public String getProcessedFile() {
        return processedFile;
    }
     
    @Async
    public String process(TreeMap<String, String> attributes, String addition) throws IOException, InterruptedException{
        System.out.println(Thread.currentThread().getName());   
        progress++;
        File folder = new File("data/pendings");
        numberOfFiles = folder.list().length + 1;
        LogConfig conf = new LogConfig(attributes, addition);
        String configuration = conf.getConfig().getAbsolutePath();
        for (String folderName : folder.list()){
        	for (String fileName : new File("data/pendings/" + folderName).list()){        
        		if (fileName.endsWith(".log")){   
        			progress++;
        			processedFile = fileName;
        			System.out.println(folderName + "/" + fileName + " is being logstashed.");
        			PrintWriter writer = new PrintWriter("script" + folderName + fileName + ".sh", "UTF-8");     
        			writer.println("#!/bin/sh ");
        			writer.println("logstash agent -f " +configuration+ " < " + System.getProperty("user.dir")+ "/data/pendings/" + folderName + "/" + fileName);
        			writer.close();
        			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "script" + folderName + fileName + ".sh");
        			Process p = pb.start();           
        			try {   
        				p.waitFor();
        			} catch (InterruptedException ex) {
        				java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        			}
        			File file = new File("script" + folderName + fileName + ".sh");
        			file.delete();
        			file = new File("data/pendings/" + folderName + "/" +fileName);
        			file.delete();
        		}   
        		File file = new File("data/pendings/" +folderName);
        		file.delete();
        	}     
        } 
        progress++;
        System.out.println("Finished");
        return "progress";
    }
}
