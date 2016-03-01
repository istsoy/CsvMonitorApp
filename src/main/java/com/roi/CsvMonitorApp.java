package com.roi;

import com.roi.services.CsvService;
import com.roi.services.XmlService;
import com.roi.threadpool.FileThreadPool;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class CsvMonitorApp {
    private static File inputFolder;
    private static File outputFolder;
    private static final long pollingInterval;

    static {
        pollingInterval = 1000;
    }

    public static File getInputFolder() {
        return inputFolder;
    }
    public static File getOutputFolder() {
        return outputFolder;
    }

    public static void main(String[] args) {
        // Get input/output folders values from config.properties
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("config.properties");
        try {
            properties.load(input);
            inputFolder = new File(properties.getProperty("input"));
            outputFolder = new File(properties.getProperty("output"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!inputFolder.exists()) {
            System.err.println("Directory not found " + inputFolder);
            System.exit(0);
        }
        if (!outputFolder.exists()) {
            System.err.println("Directory not found" + outputFolder);
            System.exit(0);
        }
        System.out.println("Input folder: " + inputFolder);
        System.out.println("Output folder: " + outputFolder + "\n");

        // Add tasks for every existing file in inputFolder to thread pool
        final FileThreadPool pool = new FileThreadPool();
        System.out.println(inputFolder + " is being monitored. Type \"stop\" to terminate.\n");
        for (File file : inputFolder.listFiles()) {
            if (CsvService.isCsv(file) || XmlService.isXml(file)) {
                pool.addTask(file);
            }
        }

        // Start monitoring inputFolder for changes
        FileAlterationObserver observer = new FileAlterationObserver(inputFolder);
        FileAlterationMonitor monitor =
                new FileAlterationMonitor(pollingInterval);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                try {
                    System.out.println("File created: " + file.getCanonicalPath());
                    if (CsvService.isCsv(file) || XmlService.isXml(file)) {
                        pool.addTask(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }

            @Override
            public void onFileChange(File file) {
                try {
                    System.out.println("File changed: " + file.getCanonicalPath());
                    if (CsvService.isCsv(file) || XmlService.isXml(file)) {
                        pool.addTask(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }

            @Override
            public void onFileDelete(File file) {
                try {
                    System.out.println("File removed: " + file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        };

        try {
            observer.addListener(listener);
            monitor.addObserver(observer);
            monitor.start();

            Scanner scanner = new Scanner(System.in);
            String userInput;
            boolean repeat = true;
            while (repeat) {
                userInput = scanner.nextLine();
                repeat = !userInput.equalsIgnoreCase("stop");
            }
            monitor.stop();
            pool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


