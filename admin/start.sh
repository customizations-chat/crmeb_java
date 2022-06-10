#!/bin/sh
url=${ADMIN_URL_ENV}
sed -i  "s/ADMIN_URL/${url}/g" `grep -rl "ADMIN_URL" /usr/nginx/wwwroot/static/js/`
nginx -g "daemon off;"


