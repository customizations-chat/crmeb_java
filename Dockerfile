FROM alpine:3.8 AS downloader

ENV MAVEN_VERSION=3.6.3
WORKDIR /data/
RUN apk add curl 
RUN curl -L -O  \
         -k "https://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" 
RUN tar -xvf apache-maven-${MAVEN_VERSION}-bin.tar.gz


FROM adoptopenjdk/openjdk8 as builder
ENV MAVEN_VERSION=3.6.3

RUN mkdir /usr/local/apache-maven-${MAVEN_VERSION}
COPY --from=downloader /data/apache-maven-${MAVEN_VERSION} /usr/local/apache-maven-${MAVEN_VERSION}
COPY docker/maven-settings.xml /usr/local/apache-maven-${MAVEN_VERSION}/conf/settings.xml
ENV MAVEN_HOME=/usr/local/apache-maven-${MAVEN_VERSION}
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

WORKDIR /data
COPY crmeb/crmeb-admin /data/crmeb-admin
COPY crmeb/crmeb-common /data/crmeb-common
COPY crmeb/crmeb-front /data/crmeb-front
COPY crmeb/crmeb-service /data/crmeb-service
COPY crmeb/crmebimage /data/crmebimage
ADD  crmeb/pom.xml /data/

RUN mvn install -Dmaven.test.skip=true

# runtime
FROM adoptopenjdk/openjdk8

WORKDIR /data
COPY --from=builder /root/.m2/repository/com/zbkj/crmeb-admin/0.0.1-SNAPSHOT/crmeb-admin-0.0.1-SNAPSHOT.jar /data/crmeb-admin.jar
COPY --from=builder /root/.m2/repository/com/zbkj/crmeb-front/0.0.1-SNAPSHOT/crmeb-front-0.0.1-SNAPSHOT.jar /data/crmeb-front.jar
COPY --from=builder /root/.m2/repository/com/zbkj/crmeb-service/0.0.1-SNAPSHOT/crmeb-service-0.0.1-SNAPSHOT.jar /data/crmeb-service.jar
COPY --from=builder /root/.m2/repository/com/zbkj/crmeb-common/0.0.1-SNAPSHOT/crmeb-common-0.0.1-SNAPSHOT.jar /data/crmeb-common.jar

COPY ./docker/start.sh /data/
RUN chmod +x /data/start.sh
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

EXPOSE 80


CMD [ "./start.sh" ]

