package com.roi.pojo.output;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="log")
public class OutputLog {
    private List<DailyRecord> dailyRecords;

    @XmlElement(name="daily-record")
    public List<DailyRecord> getDailyRecords() {
        return dailyRecords;
    }

    public void setDailyRecords(List<DailyRecord> dailyRecords) {
        this.dailyRecords = dailyRecords;
    }
}
