# Neck 
This application works with [Elasticsearch 2.2.0](https://download.elasticsearch.org/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/2.2.0/elasticsearch-2.2.0.tar.gz), [Logstash 2.2.0](https://download.elastic.co/logstash/logstash/logstash-2.2.0.tar.gz) and [Bro 2.4.1](http://knowm.org/how-to-install-bro-network-security-monitor-on-ubuntu/)  
.bashrc must contain enviroment variables:  

export LOGSTASH_HOME=/dir/logstash  
export PATH=$PATH:$LOGSTASH_HOME/bin  
export BRO_HOME=/dir/bro  
export PATH=$PATH:$BRO_HOME/bin  

Prerequisites to run this app : Java 7, Maven  

`$ sudo apt-get install oracle-java7-installer`  
`$ sudo apt-get install maven`  

then execute `$ mvn tomcat7:run` to run the app  
Running app is then available at http://localhost:8080/Neck/
