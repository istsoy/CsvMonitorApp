package com.roi.pojo.input;

import com.roi.util.DateTimeUtil;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlType(propOrder = {"timestamp", "userID", "url", "session"})
public class Record implements Comparable<Record> {
    private long timestamp;
    private String userID;
    private String url;
    private long session;

    public Record() {}

    public Record(long timestamp, String userID, String url, long timeSpent) {
        this.timestamp = timestamp;
        this.userID = userID;
        this.url = url;
        this.session = timeSpent;
    }

    @XmlElement
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @XmlElement
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement
    public long getSession() {
        return session;
    }

    public void setSession(long session) {
        this.session = session;
    }

    public int compareTo(Record o) {
        String firstId = this.getUserID();
        String secondId = o.getUserID();

        String firstLetters;
        Integer firstDigits = null;
        String secondLetters;
        Integer secondDigits = null;

        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(firstId);
        if (matcher.find()) {
            firstLetters = firstId.substring(0, matcher.start());
            firstDigits = Integer.valueOf(firstId.substring(matcher.start(), firstId.length()));
        } else {
            firstLetters = firstId;
        }

        matcher.reset(secondId);
        if (matcher.find()) {
            secondLetters = secondId.substring(0, matcher.start());
            secondDigits = Integer.valueOf(secondId.substring(matcher.start(), secondId.length()));
        } else {
            secondLetters = secondId;
        }

        int result = firstLetters.compareTo(secondLetters);
        if (result == 0) {
            if (firstDigits != null && secondDigits != null) {
                return firstDigits.compareTo(secondDigits);
            } else if (firstId.length() < secondId.length()){
                return -1;
            } else if (firstId.length() > secondId.length()) {
                return 1;
            } else {
                return 0;
            }
        }
        else
            return result;
    }

    @Override
    public String toString() {
        return DateTimeUtil.getDate(timestamp) + "," + userID + "," + url + "," + session +"\n";
    }
}
