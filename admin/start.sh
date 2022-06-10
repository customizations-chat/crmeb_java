#!/bin/sh
url=${ADMIN_URL_ENV}
sed -i "s/ADMIN_URL/${url}/g" `grep -rl "ADMIN_URL" /usr/nginx/wwwroot/static/js/`
sleep 5
nginx -g "daemon off;"


