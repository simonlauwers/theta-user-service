#!/bin/bash

RESPONSE_CODE=$(curl --write-out '%{http_code}' --silent --output /dev/null theta-risk.com)

if [[ $RESPONSE_CODE -ne 200  ]]
then
  echo "Cluster is down, skipping deployment..."
  exit 0
else
  echo "Cluster is up, deploying..."
  echo "IMAGE IS"
  echo $1
  gcloud container clusters get-credentials --region=europe-west1 risk-cluster
  if [ $2 = "staging" ]; then
    echo "Deploying staging env with image $1"
    kubectl set image deployment/user-service-deployment "game-service=$1" -n=staging
  else
    echo "Deploying production env with image $1"
    kubectl set image deployment/user-service-deployment "game-service=$1" -n=production
  fi
fi
