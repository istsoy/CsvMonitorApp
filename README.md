# CsvMonitorApp
Java program for monitoring specified folder for log files, processing these files and generating output logs (.csv + .xml)

How to use:
All you have to do is to run run.bat file located in the root directory of the project. It is necessary that you have jdk and maven installed on your PC in order to run this application.

You will find config.properties file under src/main/resources/, where you can specify input/ouput folders of your choice. By default, they point to src/main/resources/sampleInput and sampleOutput. SampleInput folder already contains .csv and .xml files for brief testing.
You can freely add, modify or delete input files. Once you are done, type "stop" to quit program.
