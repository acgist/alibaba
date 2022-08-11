# 服务注册发现Boot

通过`Nacos`提供服务注册发现以及配置中心功能

## 自动关机

服务下线默认自动关机

## 机器编号

默认会为每个服务设置一个机器编号，用来生成`ID`，每次启动一个服务机器编号都会递增，也可以通过配置`system.sn`手动设置。

## 服务调用

参考`boot-api`模块

## 配置中心

使用`Nacos`配置中心

## 服务端口

|模块|端口|
|:-|:-|
|gateway|8888|
|rest-oauth2|9999|
|web|[18000,19000)|
|rest|[19000,20000)|