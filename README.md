# Inbox App Configuration

## To run application on localhost

#### Application Configuration

- Create `application.yml` in `/src/main/resources/`
- cmd : `touch /src/main/resources/application.yml`
- Paste the following content

```yml
spring:

  config:
    import: optional:secrets.properties

  application:
    name: cloud-inbox-app

  data:
    cassandra:
      connection:
        connect-timeout: 10s
        init-query-timeout: 10s
      contact-points: ${cassandra.host}
      keyspace-name: main
      local-datacenter: datacenter1
      username: ${cassandra.user}
      password: ${cassandra.password}
      request:
        timeout: 10s
      #schema-action: RECREATE
      schema-action: CREATE_IF_NOT_EXISTS

  security:
    oauth2:
      client:
        registration:
          auth0:
            redirectUri: ${oauth.redirectUri}
            client-id: ${oauth.client-id}
            client-secret: ${oauth.client-secret}
            scope:
              - openid
              - profile
              - email
        provider:
          auth0:
            issuer-uri: ${oauth.issuer-uri}

custom:
  logout-url: ${custom.logout}
```

- create secret properties file
```bash
touch src/main/resources/secrets.properties
```

- Content

```properties
cassandra.host:localhost
cassandra.user:cassandra
cassandra.password:
oauth.redirectUri:[Domain]/login/oauth2/code/auth0
oauth.clientid:
oauth.client-secret:
oauth.issuer-uri:[Domain]
custom.logout:[Domain]
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
echo "deb https://debian.cassandra.apache.org 41x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list

# Get Keys
curl https://downloads.apache.org/cassandra/KEYS | sudo apt-key add -

# Install Cassandra
sudo apt update -y && sudo apt install cassandra -y && sudo systemctl enable cassandra && sudo systemctl start cassandra 

# Check for status
nodetool status

# Edit cassandra.yaml
sudo nano /etc/cassandra/cassandra.yaml

node1
node2
node3

#node-1
sudo sed -i 's/seeds: "127.0.0.1:7000"/seeds: "node2,node3"/g' /etc/cassandra/cassandra.yaml
sudo sed -i 's/listen_address: localhost/listen_address: node1/g' /etc/cassandra/cassandra.yaml

#node-2
sudo sed -i 's/seeds: "127.0.0.1:7000"/seeds: "node1,node3"/g' /etc/cassandra/cassandra.yaml
sudo sed -i 's/listen_address: localhost/listen_address: node2/g' /etc/cassandra/cassandra.yaml

#node-3
sudo sed -i 's/seeds: "127.0.0.1:7000"/seeds: "node2,node1"/g' /etc/cassandra/cassandra.yaml
sudo sed -i 's/listen_address: localhost/listen_address: node3/g' /etc/cassandra/cassandra.yaml

# restart cassandra
sudo systemctl restart cassandra 

# login to cassandra
cqlsh -u cassandra

# create keyspace
CREATE KEYSPACE main WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'replication_factor' : 2 };

```

## Setup Compute

```bash
git clone https://github.com/dev117uday/inbox-app.git && cd inbox-app

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

- restart nginx : 
```bash
sudo systemctl restart nginx
```

## Nginx conf for Load balancer

```bash
sudo apt-get update -y
sudo apt-get install nginx snapd -y
sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
sudo certbot --nginx

sudo rm /etc/nginx/nginx.conf && sudo nano /etc/nginx/nginx.conf
```

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
        ssl_certificate /etc/letsencrypt/live/chat.solvepao.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/chat.solvepao.com/privkey.pem;
        ssl_protocols TLSv1.3;
        location / {
            proxy_pass http://backend;         
        }
    }
}
events { }
```

- restart nginx : 

```bash
sudo systemctl restart nginx
```

- **Register application on [auth](https://auth0.com/)**

- Allowed callback URL ( on auth0 site ) : `http://[domain]/login/oauth2/code/auth0`
- Allowed Logout URL ( on auth0 site  ) : `http://[domain]/`
- Run maven install to complete setup