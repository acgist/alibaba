#!/bin/bash

# 结束任务
index=0
processNumber=`ps -aux | grep "${project.artifactId}" | grep -v grep | awk '{print $2}' | wc -l`
while [ $processNumber -ge 1 ]
do
  sleep 1
  if [ $index -le 10 ]; then
    # 优雅关机
    ps -aux | grep "${project.artifactId}" | grep -v grep | awk '{print $2}' | xargs kill -15
  else
    # 强制关机
    ps -aux | grep "${project.artifactId}" | grep -v grep | awk '{print $2}' | xargs kill -9
  fi
  index=$((index+1))
done
