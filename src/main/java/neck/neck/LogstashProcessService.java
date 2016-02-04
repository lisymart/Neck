package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class LogstashProcessService{
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");
    private List<String> lines = new ArrayList<>();
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
    public String process(Date date, TreeMap<String, String> attributes, String addition) throws IOException, InterruptedException{
        System.out.println(Thread.currentThread().getName());   
        progress++;
        for (String line : Files.readAllLines(Paths.get("paths.txt"), Charset.forName("ISO-8859-1"))) {lines.add(line);}
        String logstashPath = lines.get(1);
        File folder = new File(dateFormat.format(date));
        numberOfFiles = folder.list().length + 1;
        LogConfig conf = new LogConfig(attributes, addition);
        String configuration = conf.getConfig().getAbsolutePath();
        for (String fileName : folder.list()){        
            if (fileName.endsWith(".log")){   
            	progress++;
            	processedFile = fileName;
            	System.out.println(fileName + " is being logstashed.");
            	String logstashCommand = logstashPath + " agent -f " +configuration+ " < " + dateFormat.format(date) + "/" +fileName;
            	System.out.println(logstashCommand);
            	PrintWriter writer = new PrintWriter("script" + hourFormat.format(date) +".sh", "UTF-8");     
            	writer.println("#!/bin/sh ");
            	writer.println(logstashCommand);
            	writer.close();
            	ProcessBuilder pb = new ProcessBuilder("/bin/bash", "script" + hourFormat.format(date) +".sh");
            	Process p = pb.start();           
            	try {   
            		p.waitFor();
            	} catch (InterruptedException ex) {
            		java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
            	}
            	File file = new File("script" + hourFormat.format(date) +".sh");
            	file.delete();
            	file = new File(dateFormat.format(date) + "/" +fileName);
            	file.delete();
            }   
        }      
        folder.delete();
        File file = new File("configFile" + hourFormat.format(date) +".conf");
        file.delete();
        file = new File(dateFormat.format(date));
        file.delete();
        progress++;
        System.out.println("Finished");
        return "progress";
    }
    
    
    
    

}
