# hlrdb

## hlr-db-pool 完成----
数据库连接池提供
封装连接和db操作
解析文件格式 hlrdbpool.prop文件
````
[dbconfig]
watch=1
pool=druid

[name]数据库连接别名
type=mysql/pgsql/sqlserver
url=
username=
password=
driver
activetime=15000
availablecount=0
connectLifeTime= 1

````
后续任务：
1、补充线程池c3p0

## hlr-orm-starter
封装orm框架 未开始
思考-想做成什么样子  - 接口-xml 对应？

