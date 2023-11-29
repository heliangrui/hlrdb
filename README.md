# hlrdb

## hlr-db-pool
数据库连接池提供
封装连接和db操作
解析文件格式 hlrdbpool.prop文件
````
[dbconfig]
watch=1
pooltool=druid

[name]数据库连接别名
type=mysql/pgsql/sqlserver
kmstoken= 11111111
url=
username=
password=
driver
activetime=15000
availablecount=0
connectLifeTime= 1

````


## hlr-orm-starter
封装orm框架