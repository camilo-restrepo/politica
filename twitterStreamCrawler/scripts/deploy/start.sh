cd ../../
java -Dfile.encoding=UTF-8 -jar -Xms100m -Xmx100m -XX:MaxPermSize=70m target/twitterStreamCrawler-0.1.jar data/NRC.txt data/Translate.csv &
