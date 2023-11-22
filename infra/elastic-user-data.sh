#! /bin/bash
yum update -y && yum upgrade -y
yum install -y docker
service docker start && chkconfig docker on
usermod -a -G docker ec2-user
docker run --restart=always -d -p 9200:9200 -m 3GB -e ELASTIC_PASSWORD=MagicWord -e "discovery.type=single-node" -e "xpack.security.transport.ssl.enabled=false" elasticsearch:8.11.1
