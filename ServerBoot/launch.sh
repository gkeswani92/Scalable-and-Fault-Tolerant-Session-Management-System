#!/bin/bash

#https://aws.amazon.com/marketplace/ordering?productId=f37c8255-1ff9-48bd-b5da-b5046f4fee68&ref_=dtl_psb_continue&region=us-east-1
AMI=ami-08111162
S3_BUCKET=cs5300s16-bi49-tmm259-gk368
NUM_INSTANCES=3

aws configure set aws_access_key_id AKIAISOAOQKZPNRSDTCA
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1

#aws s3 cp project1b.war s3://${S3_BUCKET}/Session_Management.war, NOT NEEDED done in install-my-app.sh already
aws ec2 run-instances --image-id ${AMI} --count $NUM_INSTANCES --instance-type t2.micro --user-data file://install-my-app.sh --key-name CS5300_Proj1_bi49