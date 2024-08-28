javac -d dist SimpleHttpServer.java
echo "dist/: `ls dist`"
jar cvfm SimpleHttpServer.jar MANIFEST.MF -C dist .
ls -ltr
wget  https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/1.0.1/jmx_prometheus_javaagent-1.0.1.jar -O ./lib/jmx_exporter.jar
java -javaagent:./lib/jmx_exporter.jar=9993:./conf/jmx.yaml -jar SimpleHttpServer.jar