# Neck 
This application works with Elasticsearch 2.0.0, Logstash 2.1.0, Bro 2.4.1  
.bashrc must contain enviroment variables:  

export LOGSTASH_HOME=/dir/logstash  
export PATH=$PATH:$LOGSTASH_HOME/bin  
export BRO_HOME=/dir/bro  
export PATH=$PATH:$BRO_HOME/bin  
export JAVA_HOME=/dir/java-7-oracle  
export HADOOP_HOME=/dir/hadoop-2.6.0  
export PATH=$PATH:$HADOOP_HOME/bin  

Prerequisites to run this app : Java 7, Maven  

`$ sudo apt-get install oracle-java7-installer`  
`$ sudo apt-get install maven`  

then execute `$ mvn tomcat7:run` to run the app  
Running app is then available at http://localhost:8080/Neck/
