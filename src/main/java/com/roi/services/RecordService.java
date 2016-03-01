package com.roi.services;

import com.roi.pojo.output.OutputRecord;
import com.roi.pojo.input.Record;

import java.util.*;

import com.roi.util.DateTimeUtil;

public class RecordService {
    private static final long millisInSecond = 1000;

    /**
     *
     * @param initialList Initial list of records from input csv-file
     * @return This returns a map, where key is date, and value is a List of records made that day.
     */
    public static Map<String, List<OutputRecord>> getResultList(List<Record> initialList) {
        Map<String, List<OutputRecord>> resultMap = new TreeMap<String, List<OutputRecord>>();
        for (Map.Entry<String, List<Record>> dailyPair : getDailyRecords(initialList).entrySet()) {
            List<OutputRecord> list = resultMap.get(dailyPair.getKey());

            if (list == null) {
                resultMap.put(dailyPair.getKey(), list = new ArrayList<OutputRecord>());
            }
            for (Map.Entry<String, List<Record>> userPair : getUserRecords(dailyPair.getValue()).entrySet()) {
                for (Map.Entry<String, Long> urlPair : getUrlRecords(userPair.getValue()).entrySet()) {
                    OutputRecord record = new OutputRecord(userPair.getKey(), urlPair.getKey(), urlPair.getValue());
                    list.add(record);
                }
            }
        }
        return resultMap;
    }

    // This returns a map which contains lists of records (value) sorted by days (key)
    private static Map<String, List<Record>> getDailyRecords(List<Record> initialList) {
        Map<String, List<Record>> recordsByDay = new TreeMap<String, List<Record>>();
        for(Record record : initialList) {
            long timestamp = record.getTimestamp();
            long session = record.getSession();
            String firstDate = DateTimeUtil.getDate(timestamp);
            String secondDate = DateTimeUtil.getDate(timestamp+session);
            if (firstDate.equals(secondDate)) {
                List<Record> dailyRecords = recordsByDay.get(firstDate);
                if (dailyRecords == null) {
                    recordsByDay.put(firstDate, dailyRecords = new ArrayList<Record>());
                }
                record.setSession(record.getSession() / millisInSecond);
                dailyRecords.add(record);
            }
            else {
                // If session is in between two dates, split record up in 2 records
                long delta = DateTimeUtil.getDelta(timestamp, session);
                // first entry...
                List<Record> firstDailyRecords = recordsByDay.get(firstDate);
                if (firstDailyRecords == null) {
                    recordsByDay.put(firstDate, firstDailyRecords = new ArrayList<Record>());
                }
                Record newRecord = new Record(timestamp, record.getUserID(), record.getUrl(), delta);
                firstDailyRecords.add(newRecord);

                // second entry...
                List<Record> secondDailyRecords = recordsByDay.get(secondDate);
                if (secondDailyRecords == null) {
                    recordsByDay.put(secondDate, secondDailyRecords = new ArrayList<Record>());
                }
                Record secondNewRecord = new Record(timestamp, record.getUserID(), record.getUrl(),
                        session / millisInSecond - delta);
                secondDailyRecords.add(secondNewRecord);
            }
        }
        return recordsByDay;
    }

    // This returns a map which contains lists of records (value) sorted by users (key)
    private static Map<String, List<Record>> getUserRecords(List<Record> initialList) {
        Map<String, List<Record>> userRecords = new TreeMap<String, List<Record>>();
        for (Record record : initialList) {
            List<Record> list = userRecords.get(record.getUserID());
            if(list == null) {
                userRecords.put(record.getUserID(), list = new ArrayList<Record>());
            }
            list.add(record);
        }
        return userRecords;
    }

    // This returns a map which contains lists of records (value) sorted by urls (key)
    // This also calculates the average amount of time user spent on particular web-page
    private static Map<String, Long> getUrlRecords(List<Record> initialList) {
        Map<String, Long> urlRecords = new TreeMap<String, Long>();
        Map<String, Long> entriesAmount = new TreeMap<String, Long>();
        for (Record record : initialList) {
            Long session = record.getSession();
            Long previousSession = urlRecords.get(record.getUrl());
            if (previousSession == null) {
                urlRecords.put(record.getUrl(), session);
                entriesAmount.put(record.getUrl(), 1L);
            } else {
                urlRecords.put(record.getUrl(), session + previousSession);
                Long amount = entriesAmount.get(record.getUrl());
                entriesAmount.put(record.getUrl(), ++amount);
            }
        }

        for (Map.Entry<String, Long> pair : urlRecords.entrySet()) {
            Long amount = entriesAmount.get(pair.getKey());
            urlRecords.put(pair.getKey(), pair.getValue() / amount);
        }
        return urlRecords;
    }
}
