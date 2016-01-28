/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neck.neck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProgressController {
    
    @Autowired
    LogstashProcessService lps;
        
    @ResponseBody
    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    public ModelAndView progress(){
        //Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        
        String progress = "In progress...";
        if (lps.getProgress() == 0) {progress =  "File is being processed by Bro network controller...";}
        if (lps.getProgress() > 0 ) {progress =  "File is being Logstashed. [" + lps.getProgress() + " / " + lps.getNumberOfFiles() + "] -- " +  lps.getProcessedFile();}
        if (lps.getProgress() < 0 ) {progress =  "Your file is not being processed. There must have an error occured.";}
        if (lps.getProgress() > lps.getNumberOfFiles()) {progress =  "Done";}
        return new ModelAndView("progress", "progress", progress);
    }
    
    @RequestMapping(value = "/progress", method = RequestMethod.POST)
    public String uploadNew(){
        return "loadFile";
    }
}
