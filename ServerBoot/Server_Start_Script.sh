#!/bin/bash -ex
echo "Updating"
yum update -y
echo "Updated"
aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1
echo "AWS CONFIGURED"
yum -y install tomcat8-webapps tomcat8-docs-webapp tomcat8-admin-webapps
echo "TOMCAT INSTALLED"
echo "Getting IP Addr"
wget http://169.254.169.254/latest/meta-data/local-ipv4
ip_value=$(<local-ipv4)
echo "$ip_value"
rm local-ipv4
echo "Getting AMI-Launch_Index Addr"
wget http://169.254.169.254/latest/meta-data/ami-launch-index
ami_value=$(<ami-launch-index)
echo "$ami_value"
rm ami-launch-index
echo "Getting public-hostname"
wget http://169.254.169.254/latest/meta-data/public-hostname
public_value=$(<public-hostname)
echo "$public_value"
rm public-hostname
aws configure set preview.sdb true
aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name=$ip_value,Value=$public_value,Replace=true
num_servers=4
servers_in_db=0
while [ $servers_in_db -ne $num_servers ]
do
	echo "1_servers_in_db: $servers_in_db"
	sleep 2
	servers_in_db=$(aws sdb select --select-expression "select count(*) from Server_Table" --query 'Items[0].[Attributes[0].Value]' --output text --no-paginate)
	echo "2_servers_in_db: $servers_in_db"
done
aws sdb select --select-expression "select * from Server_Table" --output text --no-paginate > servers.txt
echo "DB - Finalized"
service tomcat8 start
echo "TOMCAT STARTED"

#http://www.sdbexplorer.com/licence.html?download=6-0-DMG

#aws sdb select --select-expression "select count(*) from Server_Table" --query 'Items[0].[Attributes[0].Value]' --output text

#aws sdb delete-attributes --domain-name "Server_Table" --item-name ami_value --attributes Name=ami_value,Value=ip_value

#aws sdb get-attributes --domain-name "Server_Table" --item-name ami_value

#aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name=$ip_value,Value=$public_value,Replace=true
