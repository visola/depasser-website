FROM openjdk:7

RUN curl -Lo tomcat.zip http://mirror.cc.columbia.edu/pub/software/apache/tomcat/tomcat-7/v7.0.106/bin/apache-tomcat-7.0.106.zip && \
  unzip tomcat.zip -d /opt && \
  rm -Rf /opt/apache-tomcat-7.0.106/webapps/* && \
  chmod +x /opt/apache-tomcat-7.0.106/bin/*

RUN curl -Lo /opt/openjpa.jar https://repo1.maven.org/maven2/org/apache/openjpa/openjpa-all/2.1.0/openjpa-all-2.1.0.jar

COPY website/build/libs/website.war /opt/apache-tomcat-7.0.106/webapps/ROOT.war
COPY website/build/database /opt/database

ENV CATALINA_OPTS=-Dorg.apache.el.parser.SKIP_IDENTIFIER_CHECK=true
ENV JAVA_OPTS=-javaagent:/opt/openjpa.jar
EXPOSE 8080

CMD ["/opt/apache-tomcat-7.0.106/bin/catalina.sh", "run"]
