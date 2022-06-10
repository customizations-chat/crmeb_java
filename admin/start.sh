#!/bin/sh
sed -i "s/ADMIN_URL/${ADMIN_URL_ENV}/g" `grep -rl "ADMIN_URL" /usr/nginx/wwwroot/static/js/`

nginx -g "daemon off;"


