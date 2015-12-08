package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Service
public class AsyncFileProcess{
    private List<String> lines = new ArrayList<>();
    private Integer progress = -1;
    private Integer numberOfFiles = 0;
    private String processedFile;
    private String filePath;

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
    public String process() throws IOException, InterruptedException{
        System.out.println(Thread.currentThread().getName());        
        for (String line : Files.readAllLines(Paths.get("paths.txt"))) {lines.add(line);}
        String broPath = lines.get(0);
        String logstashPath = lines.get(1);
        String filePath = lines.get(lines.size() - 1);
        removeLastLine("paths.txt");
        System.out.println(filePath + " is being processed.");
        progress++;
        File cfg = new File("json_iso8601.bro"); 
        
        String broCommand = broPath + " -r " + filePath + " " + cfg.getAbsolutePath();            

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");
        Date date = new Date(); 
        
        PrintWriter writer = new PrintWriter("script" + hourFormat.format(date) +".sh", "UTF-8");     
        writer.println("#!/bin/sh ");
        writer.println("mkdir " + dateFormat.format(date));
        writer.println("cd " + dateFormat.format(date));
        writer.println(broCommand);
        writer.println("cd ..");
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
            
        FolderZipper zipper = new FolderZipper();
        try {
            zipper.zipFolder(dateFormat.format(date), "brout " + dateFormat.format(date) + ".zip" );
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LogConfig conf = new LogConfig();
            
        File folder = new File(dateFormat.format(date));
        numberOfFiles = folder.list().length;
        for (String fileName : folder.list()){ 
            if (fileName.endsWith(".log")){
            progress++;
            processedFile = fileName;
            System.out.println(fileName + " is being logstashed.");
            String logstashCommand = logstashPath + " agent -f " +conf.getConfig().getAbsolutePath()+ " < " + dateFormat.format(date) + "/" +fileName;
            writer = new PrintWriter("script" + hourFormat.format(date) +".sh", "UTF-8");     
            writer.println("#!/bin/sh ");
            writer.println(logstashCommand);
            writer.close();
            pb = new ProcessBuilder("/bin/bash", "script" + hourFormat.format(date) +".sh");
            p = pb.start();           
          
            try {   
                p.waitFor();
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
            }
            file = new File("script" + hourFormat.format(date) +".sh");
            file.delete();
            file = new File(dateFormat.format(date) + "/" +fileName);
            file.delete();
        }   }      
        //file = new File();
        folder.delete();
        file = new File("configFile" + hourFormat.format(date) +".conf");
        file.delete();
        file = new File(dateFormat.format(date));
        file.delete();
        progress++;
        System.out.println("Finished");
        return "progress";
    }
    
    public void removeLastLine(String fileName) throws FileNotFoundException, IOException{
        RandomAccessFile f = new RandomAccessFile(fileName, "rw");
        long length = f.length() - 1;
        byte b;
        do {                     
            length -= 1;
            f.seek(length);
            b = f.readByte();
        } while(b != 10);
        f.setLength(length+1);
        f.close();
    }
    
    

}
