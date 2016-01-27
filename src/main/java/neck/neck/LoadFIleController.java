package neck.neck;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoadFIleController {
    
    BroProcessService bps = new BroProcessService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        
    @RequestMapping(value = "/loadFile", method = RequestMethod.POST)
    public String loadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String filePath = request.getParameter("processFilePath");
        File file = new File(filePath);
        
        if(file.exists() && !file.isDirectory()){
            Files.write(Paths.get("paths.txt"), (filePath + "\n").getBytes(), StandardOpenOption.APPEND);
        
        String[] filetype = filePath.split("\\.");               
        switch (filetype[1]) {
            case "pcap" : 
                System.out.println("pcap"); 
                bps.broProcess();
                return "pcap";
            case "csv" :
                System.out.println("csv");
                return "csv";
            case "log" :
                System.out.println("log");
                return "log";
    }
        } else {
            request.setAttribute("message", "Path to file is incorrect. Try again.");
            return "loadFile";
        }
        return "loadFile";
    }
    
    
    @RequestMapping(value = "/loadFile", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Date date = bps.getDate();
        File folder = new File(dateFormat.format(date));
        List<String> list = Arrays.asList(folder.list());
        
        ModelAndView model = new ModelAndView("pcap");
        model.addObject("attributesList", list);
                
        //        response.sendRedirect("/jsp/progress.jsp");

        return model;        
    }
    
}
