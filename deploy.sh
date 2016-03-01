#!/bin/bash
echo "stopping the server ..."
/opt/drinventor/learner/bin/catalina.sh stop

echo "removing old applications .."
rm -rf /opt/drinventor/learner/webapps/api*
rm -rf /opt/drinventor/learner/webapps/learner*

echo "copying the new ones .."
cp api/target/api.war /opt/drinventor/learner/webapps
cp learner/target/learner.war /opt/drinventor/learner/webapps

#echo "starting the server .."
#/opt/drinventor/learner/bin/catalina.sh start