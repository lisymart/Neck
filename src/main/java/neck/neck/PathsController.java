package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
public class PathsController {

    @RequestMapping(value = "/paths", method = RequestMethod.POST)
    public void paths(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
               
        String broPath = request.getParameter("broPath");
        String logstashPath = request.getParameter("logstashPath");
        
        PrintWriter writerSh = new PrintWriter("scriptTest.sh", "UTF-8");     
        writerSh.println("#!/bin/sh ");
        writerSh.println(broPath + " status");
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
        for (String line : Files.readAllLines(Paths.get("broOutput.txt"))) {broLines.add(line);}
        
        writerSh = new PrintWriter("scriptTest.sh", "UTF-8");     
        writerSh.println("#!/bin/sh ");
        writerSh.println(logstashPath + " --version");
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
        for (String line : Files.readAllLines(Paths.get("logstashOutput.txt"))) {logstashLines.add(line);}
        
        if (broLines.size() == 0 && logstashLines.size() == 0) {
            request.setAttribute("message", "Both paths to Bro and Logstash are not correct. Chceck and try again.");
            request.getRequestDispatcher("paths.jsp").forward(request, response);
        } else {
            if (broLines.size() == 0){
                request.setAttribute("message", "Path to Bro is not correct. Chceck and try again.");
            request.getRequestDispatcher("paths.jsp").forward(request, response);} 
            else {
                if (logstashLines.size() == 0){
                    request.setAttribute("message", "Path to Logstash is not correct. Chceck and try again.");
                    request.getRequestDispatcher("paths.jsp").forward(request, response);} 
                else {
                    List<String> broProof = new ArrayList<>();
                    for (String line : broLines.get(0).split("\\ ")) {broProof.add(line);}
        
                    List<String> logstashProof = new ArrayList<>();
                    for (String line : logstashLines.get(0).split("\\ ")) {logstashProof.add(line);}
        
                    if (broProof.get(0).equals("Getting") && logstashProof.get(0).equals("logstash")){
                        PrintWriter writer;
                        writer = new PrintWriter("paths.txt", "UTF-8");          
                        writer.println(broPath);
                        writer.println(logstashPath);
                        writer.close();
                        
                        writer = new PrintWriter("json_iso8601.bro");
                        writer.println("@load policy/tuning/json-logs");
                        writer.println("redef LogAscii::json_timestamps = JSON::TS_ISO8601;");
                        writer.close();
                        
                        file = new File("broOutput.txt");
                        file.delete();
                        file = new File("logstashOutput.txt");
                        file.delete();
                        response.sendRedirect("loadFile.jsp");
                        }      
                    }
                }
            }
    
    }
}
