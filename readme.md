### ai讲故事项目说明
>服务器资源

跳板机|账号|密码
---|---|---
103.8.32.231:15622|dfhuang|rLxdFcXTuR3QSzy

主机 | ip|账号|密码|
---|---|---|---|
KDXF-XTFA-1 | 172.22.151.90|xtfa|xtfa@2018&|
KDXF-XTFA-2 | 172.22.151.91|xtfa|xtfa@2018&|

数据库|账号|密码|
---|---|---|
172.22.23.229:3306|rwxtfa|KA0lnH7CO#kx1B8j|

redis-哨兵|master-name|
---|---|
172.22.23.37:16001|aiyx01
172.22.23.38:16001|aiyx01
172.22.23.39:16001|aiyx01

>构建部署
1. gradle build 后 将jar上传到跳板机/tmp/jumping目录
2. 在90/91 目标机器/data/web/aifather 执行 deploy.sh