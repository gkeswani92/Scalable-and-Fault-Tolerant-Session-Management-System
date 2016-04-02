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
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name="IP",Value=$ip_value,Replace=true
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name="DNS",Value=$public_value,Replace=true
aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name="REBOOT",Value="0",Replace=true
#Set this number to be 3*number of servers
attr_expected=12
attr_in_db=0
while [ $attr_in_db -ne $attr_expected ]
do
	echo "1_attr_in_db: $attr_in_db"
	sleep 2
	attr_in_db=$(aws sdb domain-metadata --domain-name "Server_Table" --query '[AttributeValueCount]' --output text --no-paginate)
	echo "2_attr_in_db: $attr_in_db"
done
meta_data=$(aws sdb select --select-expression "select * from Server_Table" --output text --no-paginate)
echo $meta_data > server.txt
echo "DB - Finalized"
service tomcat8 start
echo "TOMCAT STARTED"
#http://www.sdbexplorer.com/licence.html?download=6-0-DMG

#aws sdb select --select-expression "select count(*) from Server_Table" --query 'Items[0].[Attributes[0].Value]' --output text

#aws sdb delete-domain --domain-name "Server_Table"

#aws sdb delete-attributes --domain-name "Server_Table" --item-name ami_value --attributes Name=ami_value,Value=ip_value

#aws sdb get-attributes --domain-name "Server_Table" --item-name ami_value

#aws sdb batch-put-attributes --domain-name "Server_Table" --items Name=$ami_value,Attributes=[{Name="IP",Value=$ip_value,Replace=true},{Name="DNS",Value=$public_value,Replace=true},{Name="REBOOT",Value='0',Replace=true}]

#aws sdb put-attributes --domain-name "Server_Table" --item-name $ami_value --attributes Name=$ip_value,Value=$public_value,Replace=true
