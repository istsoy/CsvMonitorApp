package com.roi.threadpool;

import com.roi.CsvMonitorApp;
import com.roi.pojo.output.OutputRecord;
import com.roi.pojo.input.Record;
import com.roi.services.CsvService;
import com.roi.services.RecordService;
import com.roi.services.XmlService;

import javax.xml.bind.UnmarshalException;
import javax.xml.transform.Result;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileThreadPool {
    private Runnable worker;
    private ExecutorService executorService;

    // Limit number of working threads to the total amount of 10
    public FileThreadPool() {
        executorService = Executors.newFixedThreadPool(10);
    }

    // Add new task to the thread pool
    public void addTask(File file) {
        worker = new FileThread(file);
        executorService.execute(worker);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

class FileThread implements Runnable {
    private File file;
    public FileThread(File file) {
        this.file = file;
    }

    public synchronized void run() {
        if (CsvService.isCsv(file)) {
            try {
                List<Record> list = CsvService.readCsvRecords(file);
                Map<String, List<OutputRecord>> map = RecordService.getResultList(list);

                String path = CsvMonitorApp.getOutputFolder().getAbsolutePath();
                File outputFile = new File(path + "\\avg_" + file.getName());
                CsvService.writeCsvRecords(map, outputFile);
                System.out.println(outputFile + " created");
            } catch (Exception e) {
                System.err.println("Incorrect format of .csv-file: " + file.getName());
            }
        }
        else {
            try {
                List<Record> list = XmlService.readXmlRecords(file);
                Map<String, List<OutputRecord>> map = RecordService.getResultList(list);

                String path = CsvMonitorApp.getOutputFolder().getAbsolutePath();
                File outputFile = new File(path + "\\avg_" + file.getName());
                XmlService.writeXmlRecords(map, outputFile);
                System.out.println(outputFile + " created");
            } catch (UnmarshalException e) {
                System.err.println("Incorrect format of .xml file: " + file.getName());
            }
        }
    }
}
