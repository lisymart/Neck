package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import java.util.TreeSet;


public class LogConfig {
    private File config;


    public LogConfig(String timeStamp, TreeMap<String, String> renaming, TreeSet<String> delete, TreeSet<String> uppercase, TreeSet<String> lowercase, String addition) 
    		throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writerSh = new PrintWriter("configFile.conf", "UTF-8");     
        writerSh.println("input { stdin {} }");
        writerSh.println("filter { ");
        writerSh.println("json {source => \"message\"}");
        String[] ts = timeStamp.split("->");
        writerSh.println("date { match => [\"" + ts[0] + "\", \"" + ts[1] + "\"] remove_field => [\"" + ts[0] + "\"] }");
        
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
        
        if (addition != null){
        	writerSh.println(addition);
        }
        writerSh.println("ruby {code => \"event.to_hash.keys.each { |k| event[ k.sub('.','-') ] = event.remove(k) if k.include?'.' }\"} }");
        writerSh.println("output { elasticsearch { hosts => [\"localhost:9200\"] }");
        writerSh.println("}");
        writerSh.close();

    }

    public File getConfig() {
        config = new File("configFile.conf");
        return config;
    }
    
}
