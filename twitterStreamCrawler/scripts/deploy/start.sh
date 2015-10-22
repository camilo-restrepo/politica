cd ../../
java -Dfile.encoding=UTF-8 -jar -Xms300m -Xmx300m target/twitterStreamCrawler-0.1.jar data/NRC.txt data/Translate.csv data/twitterModel &
