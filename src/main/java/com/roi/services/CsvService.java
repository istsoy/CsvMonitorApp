package com.roi.services;

import com.roi.pojo.output.OutputRecord;
import com.roi.pojo.input.Record;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvService {
    private static final Pattern csvPattern;
    private static Matcher matcher;
    private static final long millisInSecond = 1000;
    private static final String NEW_LINE = "\n";
    private static final String[] CSV_INPUT_HEADER = {"time", "ID User", "URL", "number of seconds"};
    private static final String[] CSV_OUTPUT_HEADER = {"ID User", "URL", "Average"};

    static {
        csvPattern = Pattern.compile(".+\\.csv");
        matcher = csvPattern.matcher("");
    }

    /**
     *
     * @param file File to be checked
     * @return This returns true if parameter is CSV-file, and false otherwise
     */
    public synchronized static boolean isCsv(File file) {
        matcher.reset(file.getName());
        return matcher.find();
    }

    /**
     *
     * @return This returns a list of records contained in csv-file
     */
    public static List<Record> readCsvRecords(File file) throws Exception {
        FileReader fileReader = null;
        CSVParser csvParser = null;
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(CSV_INPUT_HEADER);
        List<Record> users = new ArrayList<Record>();
        try {
            fileReader = new FileReader(file);
            csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> records = csvParser.getRecords();
            for (int i = 1; i < records.size(); i++) {
                CSVRecord record = records.get(i);
                Record user = new Record(Long.parseLong(record.get("time"))*millisInSecond, record.get("ID User"),
                        record.get("URL"), Long.parseLong(record.get("number of seconds"))*millisInSecond);
                users.add(user);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                fileReader.close();
                csvParser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return users;
    }


    /**
     *
     * @param map Map which contains list of outputSchema records (value) for each day (key)
     * @param file CSV-file the map is written to
     */
    public static void writeCsvRecords(Map<String, List<OutputRecord>> map, File file) {
        FileWriter fileWriter = null;
        CSVPrinter csvPrinter = null;
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE);
        try {
            fileWriter = new FileWriter(file);
            csvPrinter = new CSVPrinter(fileWriter, csvFormat);
            csvPrinter.printRecord(CSV_OUTPUT_HEADER);

            for (Map.Entry<String, List<OutputRecord>> pair : map.entrySet()) {
                csvPrinter.print(pair.getKey().toUpperCase());
                csvPrinter.println();
                Collections.sort(pair.getValue());
                for (OutputRecord record : pair.getValue()) {
                    List<Object> recordList = new ArrayList<Object>();
                    recordList.add(record.getUserID());
                    recordList.add(record.getUrl());
                    recordList.add(record.getAverage());
                    csvPrinter.printRecord(recordList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvPrinter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
