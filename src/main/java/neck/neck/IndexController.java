package neck.neck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
    private static File paths = new File("paths.txt");

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String line = null;
        try{
            URL url = new URL("http://localhost:9200/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            line = bufferedReader.readLine();
        } catch (ConnectException e) {            
        }        
        if (line == null) {
            request.setAttribute("message", "Elasticsearch is not running. Start ELK and try again.");
            request.getRequestDispatcher("index.jsp").forward(request, response);        
        } else {        
            if(paths.exists() && !paths.isDirectory()) { 
                response.sendRedirect("loadFile.jsp");       
            } else {
            response.sendRedirect("paths.jsp");
            }
        }
    }
}


