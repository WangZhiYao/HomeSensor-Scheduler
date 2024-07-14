# Scheduler

该模块主要用于通过 MQTT 协议对传感器进行调度。

### 功能
 - 通过 [sunrise-sunset.org](sunrise-sunset.org) 获取当天日出日落时间，在日出日落时更新传感器配置后向指定 Topic 发布
