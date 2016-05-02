/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neck.neck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
/*
 * Class BroProcessService processes asynchronously files in .pcap format into .log files.
 * @author	Martin Lisý
 */
@Service
public class BroProcessService {
    Date date = new Date(); 
    
     public BroProcessService() {
    }     

     /*
      * @return	Actual date and time.
      */
    public Date getDate() {
        return date;
    }
    
    /*
     * Asynchronous method for processing .pcap files with Bro.
     * @param filePath		Absolute path to .pcap file.
     * @return	Future object of asynchronously processed files.
     */
    @Async   
    public Future<String> broProcess(String filePath) throws IOException{
        System.out.println("[" + Thread.currentThread().getName() + "] - " + filePath + " is being processed by Bro.");
        
        // using file json_iso8601.bro to specify the format of timestampt of incomming .pcap file
        File cfg = new File("json_iso8601.bro"); 
        File pendings = new File("data/pendings");
        if (!pendings.exists()) pendings.mkdirs();
        
        // creating script with path to input .pcap file that is later executed 
        String dirPath = System.getProperty("user.dir");
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
        	BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    		while ((reader.readLine()) != null) {}
            p.waitFor();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(ShowOptionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // deleting processed script
        File file = new File("script" + filePath +".sh");
        file.delete();
        
        System.out.println("[" + Thread.currentThread().getName() + "] - " + filePath + " ~ done.");
        return new AsyncResult<String>(filePath);
    }
}
