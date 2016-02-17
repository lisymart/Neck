/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class BroProcessService {
    Date date = new Date(); 
    
     public BroProcessService() {
    }     

    public Date getDate() {
        return date;
    }
    @Async   
    public Future<String> broProcess(String filePath) throws IOException{
        System.out.println("[" + Thread.currentThread().getName() + "] - " + filePath + " is being processed by Bro.");
        File cfg = new File("json_iso8601.bro"); 
        File pendings = new File("data/pendings");
        if (!pendings.exists()) pendings.mkdirs();
        
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
            p.waitFor();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File file = new File("script" + filePath +".sh");
        file.delete();
            
        File folder = new File("data/zips");
        if (!folder.exists()) folder.mkdirs();
        
        FolderZipper zipper = new FolderZipper();
        try {
            zipper.zipFolder("data/pendings/" + filePath, "data/zips/" + filePath + ".zip" );
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("[" + Thread.currentThread().getName() + "] - " + filePath + " ~ done.");
        return new AsyncResult<String>(filePath);
    }
}
