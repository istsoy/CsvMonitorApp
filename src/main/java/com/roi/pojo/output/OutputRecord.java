package com.roi.pojo.output;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlType(propOrder = {"userID", "url", "average"})
public class OutputRecord implements Comparable<OutputRecord> {
    private String userID;
    private String url;
    private long average;

    public OutputRecord() {}

    public OutputRecord(String userID, String url, long average) {
        this.userID = userID;
        this.url = url;
        this.average = average;
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
    public long getAverage() {
        return average;
    }

    public void setAverage(long average) {
        this.average = average;
    }

    public int compareTo(OutputRecord o) {
        String firstId = this.getUserID();
        String secondId = o.getUserID();

        String firstLetters = null;
        Integer firstDigits = null;
        String secondLetters = null;
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
        return userID + "," + url + "," + average +"\n";
    }
}
