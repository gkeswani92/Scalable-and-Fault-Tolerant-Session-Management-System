#!/bin/bash -ex
sudo su
S3_BUCKET=cs5300s16-bi49-tmm259-gk368
#set to number of instances to launch (even if the installation script fails, since this script )
NUM_INSTANCES=3
NUM_ATRB_PER_INSTANCE=3
echo "Updating"
yum update -y
echo "Updated"
#Set Credentials
aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1
echo "AWS CONFIGURED"
#Install Tomcat
yum -y install tomcat8-webapps tomcat8-docs-webapp tomcat8-admin-webapps
echo "TOMCAT INSTALLED"
echo "COPYING WAR FILE (project1b.war), from S3_BUCKET into tomcat8 tomcat8-webapps!!"
aws s3 cp s3://${S3_BUCKET}/Session_Management.war /var/lib/tomcat8/webapps/Session_Management.war
sudo rm local-ipv*
sudo rm ami-launch-inde*
sudo rm public-hostnam*
echo "Getting IP Addr"
wget http://169.254.169.254/latest/meta-data/local-ipv4
ip_value=$(<local-ipv4)
echo "$ip_value"
sudo rm local-ipv*
echo "Getting AMI-Launch_Index Addr"
wget http://169.254.169.254/latest/meta-data/ami-launch-index
ami_value=$(<ami-launch-index)
echo "$ami_value"
sudo rm ami-launch-inde*
echo "Getting public-hostname"
wget http://169.254.169.254/latest/meta-data/public-hostname
public_value=$(<public-hostname)
echo "$public_value"
sudo rm public-hostnam*
aws configure set preview.sdb true
aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1
#if the current $ami_value has 3 attributes go on (assuming a put operations is atomic, when an attribute is inserted its name/val pair are inserted atomicly)
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name="IP",Value=$ip_value,Replace=true
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name="DNS",Value=$public_value,Replace=true
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name="REBOOT",Value="0",Replace=true
#attr_expectd = number of instances X attributes per key
attr_expected=$(($NUM_ATRB_PER_INSTANCE*$NUM_INSTANCES))
attr_in_db=0
while [ $attr_in_db -ne $attr_expected ]
do
	echo "1_attr_in_db: $attr_in_db"
	sleep 2
	attr_in_db=$(aws sdb domain-metadata --domain-name "Server_Table" --query '[AttributeValueCount]' --output text --no-paginate)
	echo "2_attr_in_db: $attr_in_db"
done
aws sdb select --select-expression "select * from Server_Table" --output text --no-paginate > /servers.txt
aws sdb select --select-expression "select * from Server_Table where itemName() = '$ami_value'" --output text --no-paginate > /instance_info.txt
echo "DB - Finalized"
service tomcat8 start
echo "TOMCAT STARTED"

#to debug, run locally with > " bash -x ./yourscript.sh "

#servers.txt is stored in root directory pwd='/'

#http://www.sdbexplorer.com/licence.html?download=6-0-DMG

#aws sdb select --select-expression "select count(*) from Server_Table" --query 'Items[0].[Attributes[0].Value]' --output text

#1* aws sdb delete-domain --domain-name "Server_Table" 
#1*(cont) aws sdb create-domain --domain-name "Server_Table" 

#aws sdb delete-attributes --domain-name "Server_Table" --item-name ami_value --attributes Name=ami_value,Value=ip_value

#aws sdb get-attributes --domain-name "Server_Table" --item-name ami_value

#aws sdb batch-put-attributes --domain-name "Server_Table" --items Name=$ami_value,Attributes=[{Name="IP",Value=$ip_value,Replace=true},{Name="DNS",Value=$public_value,Replace=true},{Name="REBOOT",Value='0',Replace=true}]

#aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name=$ip_value,Value=$public_value,Replace=true
