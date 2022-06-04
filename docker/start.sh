#!/bin/bash

#启动环境   # 如果需要配置数据和redis，请在 application-prod.yml中修改, 用jar命令修改即可
APP_YML=--spring.profiles.active=prod

java -jar ./$APP_NAME.jar ./application-prod-docker.yml