cd ../../
java -Dfile.encoding=UTF-8 -jar -Xms100m -Xmx100m -XX:MaxPermSize=70m target/webClassifier-1.0.jar server webClassifier.yml &
