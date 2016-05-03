/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neck.neck;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
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
     * Asynchronous method for processing .pcap files with Bro.
     * @param filePath		Absolute path to .pcap file.
     * @return	Future object of asynchronously processed files.
     */
    @Async   
    public Future<ProcessResult> broProcess(String filePath) throws IOException, InvalidExitValueException, InterruptedException, TimeoutException, ExecutionException{
    	Logger logger = LoggerFactory.getLogger(BroProcessService.class);
    	logger.info(filePath + " is being processed by Bro.");;
        
        // using file json_iso8601.bro to specify the format of timestampt of incomming .pcap file
        File cfg = new File("json_iso8601.bro"); 
        File pendings = new File("data/pendings");
        if (!pendings.exists()) pendings.mkdirs();
        
        // creating script with path to input .pcap file that is later executed 
        String dirPath = System.getProperty("user.dir");
        
        new ProcessExecutor().command("mkdir", dirPath + "/data/pendings/" + filePath).execute();
        File file = new File(dirPath + "/data/pendings/" + filePath);
        Future<ProcessResult> output = new ProcessExecutor().directory(file).command("bro", "-r", dirPath + "/data/uploads/" + filePath, cfg.getAbsolutePath())
                .readOutput(true).start()
                .getFuture(); 
        
        String line = output.get(60, TimeUnit.SECONDS).outputUTF8();
        logger.info(line);
        

        logger.info(filePath + " ~ done.");
        return output;
    }
}
