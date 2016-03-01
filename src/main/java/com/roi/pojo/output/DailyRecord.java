package com.roi.pojo.output;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class DailyRecord {
    private List<OutputRecord> records;
    private String date;
    public DailyRecord() {}

    public DailyRecord(String date) {
        this.date = date;
    }

    @XmlElement(name="record")
    public List<OutputRecord> getRecords() {
        return records;
    }

    public void setRecords(List<OutputRecord> records) {
        this.records = records;
    }

    @XmlAttribute
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
