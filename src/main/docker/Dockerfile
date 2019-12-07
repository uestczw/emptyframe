FROM kdvolder/jdk8
VOLUME /tmp
ADD emptyframe.jar emptyframe.jar
RUN sh -c 'touch /emptyframe.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /emptyframe.jar" ]