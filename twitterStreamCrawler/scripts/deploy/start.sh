cd ../../
java -Dfile.encoding=UTF-8 -jar -Xms400m -Xmx400m target/twitterStreamCrawler-0.1.jar data/NRC.txt data/Translate.csv data/twitterModel &
