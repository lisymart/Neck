package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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


    public LogConfig(TreeMap<String, String> input) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writerSh = new PrintWriter("configFile.conf", "UTF-8");     
        TreeMap <String, String> renameMap = new TreeMap<String, String>();
        ArrayList <String> removeList = new ArrayList<String>();
        
        for (String key : input.keySet()){
        	String value = input.get(key);
        	if (value.equals("r")) {
        		removeList.add(key);
        	} else if (!value.isEmpty()) {
        		renameMap.put(key, value);
        	}
        }        
        
        writerSh.println("input { stdin {} }");
        writerSh.println("filter { ");
        writerSh.println("json { source => \"message\" }");
        writerSh.println("date { match => [\"ts\", \"ISO8601\"] remove_field => [\"ts\"] }");
        writerSh.println("mutate { ");
        if (!renameMap.isEmpty()) {
        	for (String fieldName : renameMap.keySet()){
        		writerSh.println("rename => {\"" + fieldName + "\" => \"" + renameMap.get(fieldName) +"\"} ");
        	}
        	}
        if (!removeList.isEmpty()){
        	writerSh.println("remove_field => [");
        	for (int i = 0; i<removeList.size() - 1; i++){
        		writerSh.println("\"" + removeList.get(i) + "\",");
        	}
        	writerSh.println("\"" + removeList.get(removeList.size() - 1) + "\"]");
        }
        writerSh.println("}");        
        writerSh.println("ruby {code => \"event.to_hash.keys.each { |k| event[ k.sub('.','-') ] = event.remove(k) if k.include?'.' }\"} }");
        writerSh.println("output { elasticsearch { hosts => [\"localhost:9200\"] } }");
        writerSh.close();

    }

    public File getConfig() {
        config = new File("configFile.conf");
        return config;
    }
    
}
