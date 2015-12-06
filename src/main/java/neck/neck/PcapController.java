package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PcapController {
    
    @RequestMapping(value = "/pcap", method = RequestMethod.POST)
    public void pcap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get("paths.txt"))) {lines.add(line);}
        String broPath = lines.get(0);
        String logstashPath = lines.get(1);
        String filePath = lines.get(2);
        File cfg = new File("json_iso8601.bro"); 
        String broCommand = broPath + " process " + filePath + " " + cfg.getAbsolutePath();            

        PrintWriter writer = new PrintWriter("script.sh", "UTF-8");     
        writer.println("#!/bin/sh ");
        writer.println(broCommand);
        writer.close();
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "script.sh");
        File output = new File ("processBro.txt");
        pb.redirectOutput(output);
        Process p = pb.start();           
            
        try {   
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        }
        removeLastLine("paths.txt");
        File file = new File("script.sh");
        file.delete();
            
        lines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get("processBro.txt"))) {lines.add(line);}
        String logPath = lines.get(2);
        String[] temp = logPath.split("\\ "); 
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date(); 
        FolderZipper zipper = new FolderZipper();
        try {
            zipper.zipFolder(temp[4], "brout " + dateFormat.format(date) + ".zip" );
        } catch (Exception ex) {
            Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUnzipper unzipper = new FileUnzipper();
        unzipper.unzip("brout " + dateFormat.format(date) + ".zip", dateFormat.format(date));
           
        LogConfig conf = new LogConfig();
            
        File folder = new File(dateFormat.format(date));
        for (String fileName : folder.list()){
            String logstashCommand = logstashPath + " agent -f " +conf.getConfig().getAbsolutePath()+ " < " + dateFormat.format(date) + "/" +fileName;
            writer = new PrintWriter("script.sh", "UTF-8");     
            writer.println("#!/bin/sh ");
            writer.println(logstashCommand);
            writer.close();
            pb = new ProcessBuilder("/bin/bash", "script.sh");
            output = new File ("processLogstash.txt");
            pb.redirectOutput(output);
            p = pb.start();           
          
            try {   
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(PcapController.class.getName()).log(Level.SEVERE, null, ex);
            }
            file = new File("script.sh");
            file.delete();
            file = new File(dateFormat.format(date) + "/" +fileName);
            file.delete();
        }           
        folder.delete();
        file = new File("processBro.txt");
        file.delete();
        file = new File("processLogstash.txt");
        file.delete();
        file = new File("configFile.conf");
        file.delete();
        response.sendRedirect("success.jsp");
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
