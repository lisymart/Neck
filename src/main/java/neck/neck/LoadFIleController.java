package neck.neck;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoadFIleController {

    @RequestMapping(value = "/loadFile", method = RequestMethod.POST)
    public void loadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String filePath = request.getParameter("processFilePath");
        File file = new File(filePath);
        
        if(file.exists() && !file.isDirectory()){
        Files.write(Paths.get("paths.txt"), (filePath + "\n").getBytes(), StandardOpenOption.APPEND);        
        String[] filetype = filePath.split("\\.");               
        switch (filetype[1]) {
            case "pcap" : 
                System.out.println("pcap");
                response.sendRedirect("pcap.jsp");
                break;
            case "csv" :
                System.out.println("csv");
                break;
            case "log" :
                System.out.println("log");
                break;
    }
        } else {
            request.setAttribute("message", "Path to file is incorrect. Try again.");
            request.getRequestDispatcher("loadFile.jsp").forward(request, response);
        }
    }
}
