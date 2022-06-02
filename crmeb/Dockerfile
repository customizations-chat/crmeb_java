FROM alpine:3.8 AS downloader

ENV MAVEN_VERSION=3.6.3
WORKDIR /data/
RUN apk add curl 
RUN curl -L -O  \
         -k "https://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" 
RUN tar -xvf apache-maven-${MAVEN_VERSION}-bin.tar.gz


FROM openjdk:18-jdk-alpine3.15
ENV MAVEN_VERSION=3.6.3

RUN mkdir /usr/local/apache-maven-${MAVEN_VERSION}
COPY --from=downloader /data/apache-maven-${MAVEN_VERSION} /usr/local/apache-maven-${MAVEN_VERSION}

ENV MAVEN_HOME=/usr/local/apache-maven-${MAVEN_VERSION}
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

WORKDIR /data
RUN mkdir /data/cr
COPY  ./* /data
RUN ls

RUN mvn install