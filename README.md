# Scheduler

该模块主要用于执行各种定时任务触发更改传感器配置

### 功能
 - 通过 [sunrise-sunset.org](https://sunrise-sunset.org) 获取当天日出日落时间，在日出日落时更新传感器配置后向指定 Topic 发布
