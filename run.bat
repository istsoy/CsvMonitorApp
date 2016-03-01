schemagen src\main\java\com\roi\pojo\input\*.java -d src\main\resources\input
schemagen src\main\java\com\roi\pojo\output\*.java -d src\main\resources\output
call mvn clean install --log-file maven.log
call mvn exec:java -Dexec.mainClass="com.roi.CsvMonitorApp"