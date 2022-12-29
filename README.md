# Inbox App Configuration

## To run application on localhost

#### Application Configuration

- Create `application.yml` in `/src/main/resources/`
- cmd : `touch /src/main/resources/application.yml`
- Paste the following content

```yml
custom:
  config:
    logout-url: http://localhost:8080

spring:

  application:
    name: cloud-inbox-app

  data:
    cassandra:
      connection:
        connect-timeout: 10s
        init-query-timeout: 10s
      contact-points: localhost
      keyspace-name: main
      local-datacenter: datacenter1
      password: cassandra
      request:
        timeout: 10s
      #schema-action: RECREATE
      schema-action: CREATE_IF_NOT_EXISTS
      username: cassandra

  security:
    oauth2:
      client:
        registration:
          auth0:
            redirectUri: http://[redirect-ui]
            client-id: client-id
            client-secret: client-secret
            scope:
              - openid
              - profile
              - email
        provider:
          auth0:
            issuer-uri: https://[domain]/

```

- **Register application on [auth](https://auth0.com/)**

- Allowed callback URL ( on auth0 site ) : `http://[domain]/login/oauth2/code/auth0`
- Allowed Logout URL ( on auth0 site  ) : `http://[domain]/`
- Run maven install to complete setup

#### Database Configuration

- Run cassandra on Docker

```bash
# Run cassandra docker from bitnami
docker run -d -p 9042:9042 --net bridge --name cassandra bitnami/cassandra:latest

# Connect to container's bash
docker exec -it cassandra bash

# Connect to cassandra inside the docker
cqlsh -u cassandra

# default - password : cassandra

# Create keyspace for app
CREATE KEYSPACE main WITH REPLICATION 
  = { 'class' : 'NetworkTopologyStrategy', 
    'replication_factor' : 1 };
```

## To Deploy Application on cloud

- Create 4 Virtual Machine on GCP : [here](./gcp.md)

### Cassandra Node Setup

```bash
# Install packages
sudo apt-get update -y && sudo apt install git default-jdk maven wget lsof htop -y 

# Get Repo
echo "deb http://www.apache.org/dist/cassandra/debian 40x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list

# Get Keys
wget -q -O - https://www.apache.org/dist/cassandra/KEYS | sudo tee /etc/apt/trusted.gpg.d/cassandra.asc

# Install Cassandra
sudo apt update -y && sudo apt install cassandra -y && sudo systemctl enable cassandra && sudo systemctl start cassandra 

# Check for status
nodetool status

# Edit cassandra.yaml
sudo nano /etc/cassandra/cassandra.yaml

node1
node2
ndoe3

#node-1
sudo sed -i 's/seeds: "127.0.0.1:7000"/seeds: "node2,ndoe3"/g' /etc/cassandra/cassandra.yaml
sudo sed -i 's/listen_address: localhost/listen_address: node1/g' /etc/cassandra/cassandra.yaml

#node-2
sudo sed -i 's/seeds: "127.0.0.1:7000"/seeds: "node1,ndoe3"/g' /etc/cassandra/cassandra.yaml
sudo sed -i 's/listen_address: localhost/listen_address: node2/g' /etc/cassandra/cassandra.yaml

#node-3
sudo sed -i 's/seeds: "127.0.0.1:7000"/seeds: "node2,node1"/g' /etc/cassandra/cassandra.yaml
sudo sed -i 's/listen_address: localhost/listen_address: ndoe3/g' /etc/cassandra/cassandra.yaml

# restart cassandra
sudo systemctl restart cassandra 

# login to cassandra
cqlsh -u cassandra

# create keyspace
CREATE KEYSPACE main WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'replication_factor' : 2 };

```

### Spring Boot setup

- touch `src/main/resources/application.yml`

```bash
custom:
  config:
    logout-url: https://chat.udayyadav.one/

spring:

  application:
    name: cloud-inbox-app

  data:
    cassandra:
      connection:
        connect-timeout: 10s
        init-query-timeout: 10s
      contact-points: localhost
      keyspace-name: main
      local-datacenter: datacenter1
      password: cassandra
      request:
        timeout: 10s
      schema-action: RECREATE
      #schema-action: CREATE_IF_NOT_EXISTS
      username: cassandra

  security:
    oauth2:
      client:
        registration:
          auth0:
            redirectUri: http://[domain]/login/oauth2/code/auth0
            client-id: [client-id]
            client-secret: [client-secret]
            scope:
              - openid
              - profile
              - email
        provider:
          auth0:
            issuer-uri: https://yadav117uday.eu.auth0.com/

```

```bash
git clone https://github.com/dev117uday/inbox-app.git && cd inbox

sed -i 's/17/11/g' pom.xml 
mvn clean install package
nohup mvn spring-boot:run &

curl localhost:8080
```

### Setup Nginx for Cassandra Nodes

```bash
sudo apt-get install nginx -y
sudo rm /etc/nginx/nginx.conf && sudo nano /etc/nginx/nginx.conf
```

- Configuration for Cassandra

```bash
http {
  server {
    listen 80;
    location / {
      proxy_pass http://127.0.0.1:8080/;
    }
  }
}

events { }
```

- restart nginx : `sudo systemctl restart nginx`

## Nginx conf for Load balancer

sudo apt-get install nginx snapd -y
sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
sudo certbot --nginx

```conf
http {
    upstream backend {
        ip_hash;
        server node1 max_fails=1 fail_timeout=5s;
        server node2 max_fails=1 fail_timeout=5s;
        server node3 max_fails=1 fail_timeout=5s;
}    
    server {
        listen 443 ssl;
        ssl_certificate /etc/letsencrypt/live/chat.udayyadav.one/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/chat.udayyadav.one/privkey.pem;
        ssl_protocols TLSv1.3;
        location / {
            proxy_pass http://backend;         
        }
    }
}
events { }
```

- restart nginx : `sudo systemctl restart nginx`

- **Register application on [auth](https://auth0.com/)**

- Allowed callback URL ( on auth0 site ) : `http://[domain]/login/oauth2/code/auth0`
- Allowed Logout URL ( on auth0 site  ) : `http://[domain]/`
- Run maven install to complete setup