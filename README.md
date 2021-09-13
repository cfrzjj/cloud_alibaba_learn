# cloud_alibaba_learn
nacos1.4.1和Sentinel 1.8.0集成
基于开源代码学习项目cloud_alibaba_learn进行相应的调整，及源代码下载下来，会存在问题，导致项目运行不起来，进行相应的排错及修改，重新讲项目运行起来，原源码作者博客地址https://blog.csdn.net/liuerchong/article/details/117201257

本项目依赖的jdk版本
java version "1.8.0_144"(重要 1.8.0_2X以上的版本会导致Sentinel 运行不起来的问题)
Sentinel 1.8.0
nacos  1.4.1
springcloud及Sentinel和nacos 对应的版本关系请参考阿里开源项目官网

该项目有个cloud_payment8071模块，继承了Sentinel和nacos，并支持Sentinel动态读取nacos中配置的流控规则和降级规则

运行项目顺序
1.将项目cloudalibaba_learn\src\main\resources\需要的相关资料目录下
nacos、sentinel-dashboard-1.8.0两个文件下载到本地
比如
我的目录在
E:\javaEvn\微服务需要版本对应\nacos
E:\javaEvn\微服务需要版本对应\sentinel-dashboard-1.8.0

2.首先运行
E:\javaEvn\微服务需要版本对应\nacos\bin下的startup.bat  文件（windows命令脚本），

页面访问nacos服务
http://localhost:8848/nacos

用户名: nacos
密码:  nacos

再进入
E:\javaEvn\微服务需要版本对应\sentinel-dashboard-1.8.0文件夹

win+r  打开cmd命令窗口
输入E:     
回车
再输入 cd  E:\javaEvn\微服务需要版本对应\sentinel-dashboard-1.8.0
进入目录下，
运行  java -jar sentinel-dashboard-1.8.0.jar命令启动 sentinel服务


页面访问sentinel服务
http://localhost:8080/

用户名： sentinel
密码：   sentinel

注意事项：必须先启动nacos再启动sentinel


3.idea打开下载下来的项目，在右键maven插件的窗口，找的cloud_alibaba_learn(root)根pom,先执行clean，再执行install

打开cloud_payment8071模块的主函数，运行主函数

cloud_payment8071项目启动成功


在页面访问
http://localhost:8071/payment/hello
可以看到
sentinel已经加载了nacos的配置


