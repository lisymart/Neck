/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.springframework.stereotype.Service;

@Service
public class BroProcessService {
    private List<String> lines = new ArrayList<>();
    Date date = new Date(); 
    
     public BroProcessService() {
    }     

    public Date getDate() {
        return date;
    }
       
    public void broProcess() throws IOException{
    for (String line : Files.readAllLines(Paths.get("paths.txt"), Charset.forName("ISO-8859-1"))) {lines.add(line);}
        String filePath = lines.get(lines.size() - 1);
        System.out.println(filePath + " is being processed.");
        File cfg = new File("json_iso8601.bro"); 
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        DateFormat hourFormat = new SimpleDateFormat("HH-mm-ss");
        
        PrintWriter writer = new PrintWriter("script" + hourFormat.format(date) +".sh", "UTF-8");     
        writer.println("#!/bin/sh ");
        writer.println("mkdir " + dateFormat.format(date));
        writer.println("cd " + dateFormat.format(date));
        writer.println("bro -r " + filePath + " " + cfg.getAbsolutePath());
        writer.println("rm -r .state");
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
