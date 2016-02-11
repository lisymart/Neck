# Neck 
This application works with [Elasticsearch 2.2.0](https://download.elasticsearch.org/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/2.2.0/elasticsearch-2.2.0.tar.gz), [Logstash 2.2.0](https://download.elastic.co/logstash/logstash/logstash-2.2.0.tar.gz), [Bro 2.4.1](http://knowm.org/how-to-install-bro-network-security-monitor-on-ubuntu/), [Tachyon 0.8.2 with Hadoop 2.6.0](http://tachyon-project.org/downloads/files/0.8.2/tachyon-0.8.2-hadoop2.6-bin.tar.gz)  
.bashrc must contain enviroment variables:  

export LOGSTASH_HOME=/dir/logstash  
export PATH=$PATH:$LOGSTASH_HOME/bin  
export BRO_HOME=/dir/bro  
export PATH=$PATH:$BRO_HOME/bin  
export JAVA_HOME=/dir/java-7-oracle  
export TACHYON_HOME=/dir/tachyon  
export PATH=$PATH:$TACHYON_HOME/bin  

Prerequisites to run this app : Java 7, Maven  

`$ sudo apt-get install oracle-java7-installer`  
`$ sudo apt-get install maven`  

then execute `$ mvn tomcat7:run` to run the app  
Running app is then available at http://localhost:8080/Neck/
