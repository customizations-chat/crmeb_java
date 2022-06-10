FROM docker.io/library/node:14-buster as builder

WORKDIR /src
COPY ./ ./

# ENV YARN_CACHE_FOLDER=/tmp/yarn-cache
# RUN --mount=type=cache,sharing=locked,id=yarncache,target=/tmp/yarn-cache yarn
RUN npm config set registry https://registry.npm.taobao.org
# RUN echo "sass_binary_site=https://npm.taobao.org/mirrors/node-sass/" >> ~/.npmrc
RUN npm install
RUN npm run build:prod


FROM nginx:alpine
EXPOSE 80
COPY --from=builder /src/dist /usr/nginx/wwwroot
COPY ./nginx.conf /etc/nginx/nginx.conf
COPY ./start.sh  /start.sh
RUN chmod +x /start.sh
CMD ["/start.sh"]