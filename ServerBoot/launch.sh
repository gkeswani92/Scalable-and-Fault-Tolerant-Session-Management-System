#!/bin/bash
#!!!PLEASE SET NUM_INSTANCES ON launch.sh, install-my-app.sh, server-reboot-script.sh!!!!!
#!!!Before launching please make sure to empty the simpledb in our db by ssh'ing and prompting the followng commands:
#To make this easier we have an ec2 instance, just use sudo ssh -i "CS5300_Proj1_bi49.pem" ec2-user@ec2-52-23-151-189.compute-1.amazonaws.com
#then prompt
#aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
#aws configure set aws_secret_access_key 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
#aws configure set default.region us-east-1
#aws configure set preview.sdb true
#aws sdb delete-domain --domain-name "Server_Table" 
#aws sdb create-domain --domain-name "Server_Table" 

AMI=ami-08111162
S3_BUCKET=cs5300s16-bi49-tmm259-gk368
NUM_INSTANCES=5

aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1

#aws s3 cp project1b.war s3://${S3_BUCKET}/Session_Management.war, NOT NEEDED done in install-my-app.sh already
aws ec2 run-instances --image-id ${AMI} --count $NUM_INSTANCES --instance-type t2.micro --user-data file://install-my-app.sh --key-name CS5300_Proj1_bi49