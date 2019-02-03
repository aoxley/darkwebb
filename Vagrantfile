# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
  
  config.vm.define "elastic" do |elastic|
    elastic.vm.box = "ubuntu/trusty64"
	
	elastic.vm.provider "virtualbox" do |v|
      v.memory = 4096
      v.cpus = 2
    end

	elastic.vm.network "private_network", ip: "192.168.50.1"
	elastic.vm.network "forwarded_port", guest: 9200, host: 9200
	
	elastic.vm.provision "shell", inline: <<-SHELL
	  #install java (needed for elastic)
	  apt-get -y -q update
      apt-get -y -q upgrade
      apt-get -y -q install software-properties-common htop
      add-apt-repository ppa:webupd8team/java
      apt-get -y -q update
      echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
      apt-get -y -q install oracle-java8-installer
      update-java-alternatives -s java-8-oracle
	  
	  #install elastic
	  wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
	  apt-get -y install apt-transport-https
	  echo "deb https://artifacts.elastic.co/packages/5.x/apt stable main" | tee -a /etc/apt/sources.list.d/elastic-5.x.list
	  apt-get -y update && apt-get -y install elasticsearch
	  
	  #open it up so other vm's can connect
	  echo "network.host: 0.0.0.0" >> /etc/elasticsearch/elasticsearch.yml
	  
	  #configure it to start on load
	  update-rc.d elasticsearch defaults 95 10
	  sudo -i service elasticsearch start
	  echo "sleeping for 5 seconds to ensure elastic is up"
	  sleep 5s
	  
	  #create the index with dynamic mapping
	  #curl -X DELETE 'http://localhost:9200/_all'
	  #curl -X PUT 'localhost:9200/crawler?pretty' -H 'Content-Type: application/json' -d'{"mappings": {"onion": {"dynamic": true,"properties": {"@timestamp": {"type":"date"}}}}}'
	SHELL
  end
  
  
  config.vm.define "kibana" do |kibana|
    kibana.vm.box = "ubuntu/trusty64"

	kibana.vm.network "private_network", ip: "192.168.50.3"
	kibana.vm.network "forwarded_port", guest: 5601, host: 5601
	
	kibana.vm.provision "shell", inline: <<-SHELL
	  #install java (needed for kibana)
	  apt-get -y -q update
      apt-get -y -q upgrade
      apt-get -y -q install software-properties-common htop
      add-apt-repository ppa:webupd8team/java
      apt-get -y -q update
      echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
      apt-get -y -q install oracle-java8-installer
      update-java-alternatives -s java-8-oracle
	  
	  #install kibana
	  wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
	  apt-get -y install apt-transport-https
	  echo "deb https://artifacts.elastic.co/packages/5.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-5.x.list
	  apt-get -y update && apt-get -y install kibana
	  
	  #point it to elastic backend
	  echo "elasticsearch.url: http://192.168.50.1:9200" >> /etc/kibana/kibana.yml
	  echo "server.host: 0.0.0.0" >> /etc/kibana/kibana.yml
	  echo "kibana.index: crawler" >> /etc/kibana/kibana.yml
	  
	  
	  #configure kibana to start on load
	  update-rc.d kibana defaults 95 10
	  sudo -i service kibana start
	SHELL
  end
  
  config.vm.define "crawler" do |crawler|
	crawler.vm.box = "ubuntu/trusty64"
	
	crawler.vm.network "private_network", ip: "192.168.50.2"
	crawler.vm.network "forwarded_port", guest: 8123, host: 8123
	
	crawler.vm.provider "virtualbox" do |v|
      v.memory = 2048
      v.cpus = 4
    end
	
	crawler.vm.provision "shell", inline: <<-SHELL
	  #install tor and configure
	  apt-get -y install tor
	  sed -i 's/#SocksPort 9050/SocksPort 9050/g' /etc/tor/torrc
	  service start tor
	  
	  #install proxy and configure   https://www.marcus-povey.co.uk/2016/03/24/using-tor-as-a-http-proxy/
	  apt-get -y install polipo
	  echo "socksParentProxy = \"localhost:9050\"" >> /etc/polipo/config
	  echo "socksProxyType = socks5" >> /etc/polipo/config
	  echo "proxyAddress = \"0.0.0.0\"" >> /etc/polipo/config
	  /etc/init.d/polipo restart
	  
	  #install java so we can run the crawler.
	  apt-get -y -q update
      apt-get -y -q upgrade
      apt-get -y -q install software-properties-common htop
      add-apt-repository ppa:webupd8team/java
      apt-get -y -q update
      echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
      apt-get -y -q install oracle-java8-installer
      update-java-alternatives -s java-8-oracle
	  
	  #install maven so we can compile the code
	  apt-get update
	  apt-get -y install maven
	  mvn -f /vagrant/spider/pom.xml clean install -DskipTests
	  
	  #Run the crawler
	  cp /vagrant/spider/darkwebb.conf /etc/init/darkwebb.conf
	  #service darkwebb start
	SHELL
  end
end
