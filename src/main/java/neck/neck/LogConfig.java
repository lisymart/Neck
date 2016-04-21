package neck.neck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Class creating Logstash configuration file, where all transformations are set.
 * @author	Martin Lisý
 */
public class LogConfig {
    private File config;
    private double offset = 0.0000000001;


    // Constructor
    public LogConfig(String host, String confName, String timeStamp, TreeMap<String, String> renaming, TreeMap<String, ArrayList<String>> ranging, TreeSet<String> delete, TreeSet<String> uppercase, 
    		TreeSet<String> lowercase, TreeSet<String> anonymize, String hashingKey, String annmAlgo, TreeMap<String, String> newFields, String addition) throws FileNotFoundException, UnsupportedEncodingException {
    	   logConfig(host, confName, timeStamp, renaming, ranging, delete, uppercase, lowercase, anonymize, hashingKey, annmAlgo, newFields, addition);
    }
    
    /*
     * @param	host		Location of Elasticsearch cluster.
     * @param 	confName	Name of configuration file.
     * @param	timeStamp	Name of attribute that represents the timestamp.
     * @param	renaming	Collection of attributes that are going to be renamed.
     * @param	ranging		Collection of attributes that are going to have specified range of theirs values.
     * @param	delete		Collection of attributes that are going to be deleted.
     * @param	uppercase	Collection of attributes that are going to be uppercased.
     * @param	lowercase	Collection of attributes that are going to be lowercased.
     * @param	anonymize	Collection of attributes that are going to be converted to String and anonymized.
     * @param	hashingKey	Key used for anonymization.
     * @param	annmAlgo	Algorhitm that is used for anonymizing of attributes.
     * @param	newFields	Collection of attributes that are going to be added as new.
     * @param	addition	Code that represents transformations that Neck does not support.
     */
    public void logConfig(String host, String confName, String timeStamp, TreeMap<String, String> renaming, TreeMap<String, ArrayList<String>> ranging, TreeSet<String> delete, TreeSet<String> uppercase, 
    		TreeSet<String> lowercase, TreeSet<String> anonymize, String hashingKey, String annmAlgo, TreeMap<String, String> newFields, String addition) throws FileNotFoundException, UnsupportedEncodingException{
    	PrintWriter writerSh = new PrintWriter(confName, "UTF-8");
    	// Input definition (standard input)
        writerSh.println("input { stdin {} }");
        
        // Filter definition (transformations)
        writerSh.println("filter { ");
        writerSh.println("json {source => \"message\"}");
        
        // TimeStamp definition
        if (!timeStamp.contains("null")) {
        	String[] ts = timeStamp.split("->");
        	writerSh.println("date { match => [\"" + ts[0] + "\", \"" + ts[1] + "\"] remove_field => [\"" + ts[0] + "\"] }");
        }
        
        //Renaming transformation definition
        if (!renaming.isEmpty()) {
        	writerSh.println("mutate { ");
        	for (String fieldName : renaming.keySet()){
        		writerSh.println("rename => {\"" + fieldName + "\" => \"" + renaming.get(fieldName) +"\"} ");
        	}
        	writerSh.println("}");
        	}
        
        //Delete transformation definition
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
        
        // UpperCase transformation definition
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
        
        //Lowercase transformation definition
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
        
      //Anonymize transformation definition (first conversion to string, then anonymisation)
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
        	writerSh.println("key => \"" + hashingKey + "\"");
        	writerSh.println("}");
        }
        
        //Range transformation definition
        if(!ranging.isEmpty()){
        	writerSh.print("range { ");
        	writerSh.print("ranges => [");
        	boolean isFirst = true;
        	
        	for (String s: ranging.keySet()){
        		String lowerBound =  ranging.get(s).get(0);
        		String upperBound = ranging.get(s).get(1);
        		
        		if (lowerBound != "" && upperBound != ""){
        			if (isFirst) {
            			writerSh.println("\"" + s + "\", " +Long.MIN_VALUE + ", " + (Double.parseDouble(lowerBound) - offset) + ", " + "\"drop\"");
            			writerSh.println(",\"" + s + "\", " + (Double.parseDouble(upperBound) + offset) + ", " + Long.MAX_VALUE + ", " + "\"drop\"");
            			isFirst = false;
            		} else {
            			writerSh.println(",\"" + s + "\", " + Long.MIN_VALUE + ", " + (Double.parseDouble(lowerBound) - offset) + ", " + "\"drop\"");
            			writerSh.println(",\"" + s + "\", " + (Double.parseDouble(upperBound) + offset) + ", " + Long.MAX_VALUE + ", " + "\"drop\"");
            		}
        		} else if(lowerBound == "") {
        			if(isFirst){
        				writerSh.println("\"" + s + "\", " + (Double.parseDouble(upperBound) + offset) + ", " + Long.MAX_VALUE + ", " + "\"drop\"");
        				isFirst = false;
        			} else  {
        				writerSh.println(",\"" + s + "\", " + (Double.parseDouble(upperBound) + offset) + ", " + Long.MAX_VALUE + ", " + "\"drop\"");
        			}
        		} else if (upperBound == "") {
        			if (isFirst) {
        				writerSh.println("\"" + s + "\", " +Long.MIN_VALUE + ", " + (Double.parseDouble(lowerBound) - offset) + ", " + "\"drop\"");
        			} else {
        				writerSh.println(",\"" + s + "\", " +Long.MIN_VALUE + ", " + (Double.parseDouble(lowerBound) - offset) + ", " + "\"drop\"");
        			}
        		}
        	}
        	writerSh.println("]}");
        }
        
        //Add new field transformation definition
        if(!newFields.isEmpty()){
        	writerSh.println("mutate { add_field => {");	
        	for (String s : newFields.keySet()){
        		writerSh.println("\"" + s + "\" => \"" + newFields.get(s) + "\"");
        	}
        	writerSh.println("} }");
        }
        
        // Field for additional configuration code
        if (addition != null){
        	writerSh.println(addition);
        }
        
        // Renaming log file names. Replacing the dot in name with dash.
        writerSh.println("ruby {code => \"event.to_hash.keys.each { |k| event[ k.sub('.','-') ] = event.remove(k) if k.include?'.' }\"} }");
        
        //Output filter definition with selected Elasticsearch cluster setting.
        writerSh.println("output { elasticsearch { hosts => [\"" + host + "\"] }");
        writerSh.println("}");
        writerSh.close();
    }
    
    /*
     * @param	confName	Name of configuration file.
     * @return	Configuration file.
     */
    public File getConfig(String confName) {
        config = new File(confName);
        return config;
    }
    
}
