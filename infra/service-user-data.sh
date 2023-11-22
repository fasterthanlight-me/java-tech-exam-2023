#! /bin/bash
yum update -y && yum upgrade -y
yum install -y docker
service docker start && chkconfig docker on
usermod -a -G docker ec2-user
docker run --restart=always -d -p 8080:8080 -m 650MB -e AUTH_USER=admin -e AUTH_PASSWORD=dummy-admin-password -e ELASTIC_URL=http://18.184.219.110:9200 -e ELASIC_PASSWORD=MagicWord nazarmedykh/es-demo:v1
