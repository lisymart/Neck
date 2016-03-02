package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import java.util.TreeSet;


public class LogConfig {
    private File config;
    private double offset = 0.0000000001;



    public LogConfig(String host, String confName, String timeStamp, TreeMap<String, String> renaming, TreeMap<String, double[]> ranging, TreeSet<String> delete, TreeSet<String> uppercase, 
    		TreeSet<String> lowercase, TreeSet<String> anonymize, String annmAlgo, String addition) throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter writerSh = new PrintWriter(confName, "UTF-8");     
        writerSh.println("input { stdin {} }");
        writerSh.println("filter { ");
        writerSh.println("json {source => \"message\"}");
        
        if (!timeStamp.contains("null")) {
        	String[] ts = timeStamp.split("->");
        	writerSh.println("date { match => [\"" + ts[0] + "\", \"" + ts[1] + "\"] remove_field => [\"" + ts[0] + "\"] }");
        }
        
        if (!renaming.isEmpty()) {
        	writerSh.println("mutate { ");
        	for (String fieldName : renaming.keySet()){
        		writerSh.println("rename => {\"" + fieldName + "\" => \"" + renaming.get(fieldName) +"\"} ");
        	}
        	writerSh.println("}");
        	}
        if (!delete.isEmpty()){
        	writerSh.print("mutate { ");
        	writerSh.print("remove_field => [");
        	boolean isFirst = true;
        	for (String s: delete){
        		if (isFirst) {
        			writerSh.print("\"" + s + "\"");
        			isFirst = false;
        		} else {
        			writerSh.print(",\"" + s + "\"");
        		}
        	}
        	writerSh.println("]}");
        }
        if(!uppercase.isEmpty()){
        	writerSh.print("mutate { ");
        	writerSh.print("uppercase => [");
        	boolean isFirst = true;
        	for (String s: uppercase){
        		if (isFirst) {
        			writerSh.print("\"" + s + "\"");
        			isFirst = false;
        		} else {
        			writerSh.print(",\"" + s + "\"");
        		}
        	}
        	writerSh.println("]}");
        }
        
        if(!lowercase.isEmpty()){
        	writerSh.print("mutate { ");
        	writerSh.print("lowercase => [");
        	boolean isFirst = true;
        	for (String s: lowercase){
        		if (isFirst) {
        			writerSh.print("\"" + s + "\"");
        			isFirst = false;
        		} else {
        			writerSh.print(",\"" + s + "\"");
        		}
        	}
        	writerSh.println("]}");
        }
        if (!anonymize.isEmpty()) {
        	writerSh.println("mutate { ");
        	for (String fieldName : anonymize){
        		writerSh.println("convert => {\"" + fieldName + "\" => \"string\"} ");
        	}
        	writerSh.println("}");
        }
        
        if(!anonymize.isEmpty()){
        	writerSh.println("anonymize { ");
        	writerSh.println("algorithm => \"" + annmAlgo + "\"");
        	writerSh.println("fields => [");
        	boolean isFirst = true;
        	for (String s: anonymize){
        		if (isFirst) {
        			writerSh.print("\"" + s + "\"");
        			isFirst = false;
        		} else {
        			writerSh.print(",\"" + s + "\"");
        		}
        	}
        	writerSh.println("]");
        	writerSh.println("key => \"ThisIsMyHashingKey\"");
        	writerSh.println("}");
        }
        
        if(!ranging.isEmpty()){
        	writerSh.print("range { ");
        	writerSh.print("ranges => [");
        	boolean isFirst = true;
        	for (String s: ranging.keySet()){
        		if (isFirst) {
        			writerSh.println("\"" + s + "\", " + Integer.MIN_VALUE + ", " + (ranging.get(s)[0] - offset) + ", " + "\"drop\"");
        			writerSh.println(",\"" + s + "\", " + (ranging.get(s)[1] + offset) + ", " + Integer.MAX_VALUE + ", " + "\"drop\"");
        			isFirst = false;
        		} else {
        			writerSh.println(",\"" + s + "\", " + Integer.MIN_VALUE + ", " + (ranging.get(s)[0] - offset) + ", " + "\"drop\"");
        			writerSh.println(",\"" + s + "\", " + (ranging.get(s)[1] + offset) + ", " + Integer.MAX_VALUE + ", " + "\"drop\"");
        		}
        	}
        	writerSh.println("]}");
        }
        
        if (addition != null){
        	writerSh.println(addition);
        }
        writerSh.println("ruby {code => \"event.to_hash.keys.each { |k| event[ k.sub('.','-') ] = event.remove(k) if k.include?'.' }\"} }");
        writerSh.println("output { elasticsearch { hosts => [\"" + host + "\"] }");
        writerSh.println("}");
        writerSh.close();

    }

    public File getConfig(String confName) {
        config = new File(confName);
        return config;
    }
    
}
