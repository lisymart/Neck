package neck.neck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

@Service
public class ExternalProcessService {
	private Logger logger = LoggerFactory.getLogger(ExternalProcessService.class);
	private String dirPath = System.getProperty("user.dir");
	
	/*
	 * @param	fileName		File that is going to be processed.
	 * @param	configuration	Config file with defined transformations over the input file.
	 * @return	Future object of asynchronously log processing. 
	 */
    @Async
    public Future<ProcessResult> logstashProcess(File fileName, String configuration) throws IOException, InterruptedException, ExecutionException, TimeoutException{
       	// Creates script that processes log files with transformation config file
        logger.info(fileName.getAbsolutePath() + " is being processed by Logstash.");
        
        Future<ProcessResult> output = new ProcessExecutor("/bin/bash", "logstashExecutor.sh", configuration, fileName.getAbsolutePath())
       			.readOutput(true).start().getFuture();
        	
       	String line = output.get(60, TimeUnit.SECONDS).outputUTF8();
        logger.info(line);
        logger.info(fileName.getAbsolutePath() + " ~ done.");
        return output;
    }
    
    /*
     * Asynchronous method for processing .pcap files with Bro.
     * @param filePath		Absolute path to .pcap file.
     * @return	Future object of asynchronously processed files.
     */
    @Async   
    public Future<ProcessResult> broProcess(String filePath) throws IOException, InvalidExitValueException, InterruptedException, TimeoutException, ExecutionException{
    	logger.info(filePath + " is being processed by Bro.");;
        
        // using file json_iso8601.bro to specify the format of timestampt of incomming .pcap file
        File cfg = new File("json_iso8601.bro"); 
        File pendings = new File("data/pendings");
        if (!pendings.exists()) pendings.mkdirs();
        
        // creating script with path to input .pcap file that is later executed 
        Path path = Paths.get((dirPath + "/data/pendings/" + filePath));
        Files.createDirectories(path);
        File file = new File(dirPath + "/data/pendings/" + filePath);
        Future<ProcessResult> output = new ProcessExecutor().directory(file).command("bro", "-r", dirPath + "/data/uploads/" + filePath, cfg.getAbsolutePath())
                .readOutput(true).start()
                .getFuture(); 
        
        String line = output.get(60, TimeUnit.SECONDS).outputUTF8();
        logger.info(line);

        logger.info(filePath + " ~ done.");
        return output;
    }
    
    /*
	 * Method checkInstallations creates scripts with installation of systems check that are executed.
	 * @return	Collection of names of systems that are properly set.
	 */
    public List<String> checkInstallations() throws ServletException, IOException, InvalidExitValueException, InterruptedException, TimeoutException {  
    	logger.info("Checking the necessary installations.");
    	List<String> running = new ArrayList<String>();
    	
    	//Checking whether Bro system is properly installed and set.
    	String broOutput = new ProcessExecutor().command("broctl", "status")
                .readOutput(true).execute()
                .outputUTF8();    
        
        //Checking whether Logstash system is properly installed and set.
    	String logstashOutput = new ProcessExecutor().command("logstash", "--version")
                .readOutput(true).execute()
                .outputUTF8();    
    	
    	//Installing missing plugin
    	new ProcessExecutor().command("plugin", "install", "logstash-filter-range")
                .readOutput(true).execute();
        
               
       	if (!broOutput.contains("command")){
        	running.add("bro");
        }

       	if (!logstashOutput.contains("command")){
        	running.add("logstash");
        }
        
        //Creating configuration file for Bro that converts the format of timestamp from Unix to ISO8601.
        PrintWriter writer = new PrintWriter("json_iso8601.bro");
        writer.println("@load policy/tuning/json-logs");
        writer.println("redef LogAscii::json_timestamps = JSON::TS_ISO8601;");
        writer.close();
        
        //Adding the 755 permissions to logstashExecutor.sh script.
        Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
         
        Files.setPosixFilePermissions(Paths.get(dirPath + "/logstashExecutor.sh"), perms);
        
        //new ProcessExecutor().command("chmod", "755", dirPath + "/logstashExecutor.sh")
        //.readOutput(true).execute();
        
        return running; 
    }
}
