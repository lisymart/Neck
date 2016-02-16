package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;


public class PathsController {
    public ArrayList<String> checkInstallations() throws ServletException, IOException {        
    	ArrayList<String> running = new ArrayList<String>();
        PrintWriter writerSh = new PrintWriter("scriptTest.sh", "UTF-8");     
        writerSh.println("#!/bin/sh ");
        writerSh.println("broctl status");
        writerSh.close();
        File broOutput = new File("broOutput.txt");
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "scriptTest.sh");
        pb.redirectOutput(broOutput);
        Process p = pb.start();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(PathsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = new File("scriptTest.sh");
        file.delete();
        List<String> broLines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get("broOutput.txt"), Charset.forName("ISO-8859-1"))) {broLines.add(line);}
        
        writerSh = new PrintWriter("scriptTest.sh", "UTF-8");     
        writerSh.println("#!/bin/sh ");
        writerSh.println("logstash --version");
        writerSh.close();
        File logstashOutput = new File("logstashOutput.txt");
        pb = new ProcessBuilder("/bin/bash", "scriptTest.sh");
        pb.redirectOutput(logstashOutput);
        p = pb.start();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(PathsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        file = new File("scriptTest.sh");
        file.delete();
        List<String> logstashLines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get("logstashOutput.txt"), Charset.forName("ISO-8859-1"))) {logstashLines.add(line);}
       
        if (broLines.size() != 0){
        	List<String> broProof = new ArrayList<>();
        	for (String line : broLines.get(0).split(" ")) {broProof.add(line);}
        	if (broProof.get(0).equals("Getting")){
        		running.add("bro");
        	}
        }

        if (logstashLines.size() != 0){
        	List<String> logstashProof = new ArrayList<>();
        	for (String line : logstashLines.get(0).split(" ")) {logstashProof.add(line);}
        	if (logstashProof.get(0).equals("logstash")){
        		running.add("logstash");
        	}
        }
        
        PrintWriter writer = new PrintWriter("json_iso8601.bro");
        writer.println("@load policy/tuning/json-logs");
        writer.println("redef LogAscii::json_timestamps = JSON::TS_ISO8601;");
        writer.close();
                        
        file = new File("broOutput.txt");
        file.delete();
        file = new File("logstashOutput.txt");
        file.delete();
        
        return running; 
    }
}