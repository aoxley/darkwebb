# starting from scratch: #  
  
vagrant destroy  
vagrant up elastic  
vagrant ssh elastic  
(while inside ssh):  
curl -X PUT 'localhost:9200/crawler?pretty' -H 'Content-Type: application/json' -d'{"mappings": {"onion": {"dynamic": true,"properties": {"@timestamp": {"type":"date"}}}}}'  
exit  
  
vagrant up kibana  
(go to kibana: http://localhost:5601  )  
will want an index added. Add "crawler" as the index, and set date stuff to @timestamp  
  
vagrant up crawler  
vagrant ssh crawler  
(while inside ssh):  
sudo sh -c 'nohup java -jar -Dspring.profiles.active=prod /vagrant/spider/target/spider-0.0.1-SNAPSHOT.jar > /var/log/darkwebb.log 2>&1 &'  
exit  
