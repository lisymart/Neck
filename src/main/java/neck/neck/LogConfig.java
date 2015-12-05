package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a
 */
public class LogConfig {
    private File config;


    public LogConfig() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writerSh = new PrintWriter("configFile.conf", "UTF-8");     
        writerSh.println("input { stdin {} }");
        writerSh.println("filter { ");
        writerSh.println("json { source => \"message\" }");
        writerSh.println("date { match => [\"ts\", \"ISO8601\"] remove_field => [\"ts\"] }");
        writerSh.println("ruby {code => \"event.to_hash.keys.each { |k| event[ k.sub('.','-') ] = event.remove(k) if k.include?'.' }\"} }");
        writerSh.println("output { elasticsearch { hosts => [\"localhost:9200\"] } }");
        writerSh.close();

    }

    public File getConfig() {
        config = new File("configFile.conf");
        return config;
    }
    
}
