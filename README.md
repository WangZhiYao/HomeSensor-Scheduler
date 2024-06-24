# Scheduler

该模块主要用于通过 MQTT 协议对传感器进行调度。

### 功能
 - 订阅 `home/#` 主题，根据传感器位置与类型，将不同传感器上传的数据保存到 MongoDB 中
 - 通过 [sunrise-sunset.org](sunrise-sunset.org) 获取当天日出日落时间，判断当前时间来在日落之后控制光照传感器进行深度睡眠
