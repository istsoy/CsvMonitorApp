package com.roi.services;

import com.roi.pojo.input.Log;
import com.roi.pojo.input.Record;
import com.roi.pojo.output.DailyRecord;
import com.roi.pojo.output.OutputLog;
import com.roi.pojo.output.OutputRecord;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlService {
    private static final Pattern xmlPattern;
    private static Matcher matcher;

    static {
        xmlPattern = Pattern.compile(".+\\.xml");
        matcher = xmlPattern.matcher("");
    }

    /**
     *
     * @param file File to be checked
     * @return This returns true if parameter is XML-file, and false otherwise
     */
    public synchronized static boolean isXml(File file) {
        matcher.reset(file.getName());
        return matcher.find();
    }

    /**
     *
     * @return This returns a list of records contained in xml-file
     */
    public static List<Record> readXmlRecords(File file) throws UnmarshalException {
        List<Record> records = new ArrayList<Record>();
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            JAXBContext jaxbContext = JAXBContext.newInstance(Log.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // We set schema for unmarshaller to validate inputSchema .XML-files against generated .XSD
            try {
                Schema schema = sf.newSchema(new File("src/main/resources/inputSchema/schema1.xsd"));
                jaxbUnmarshaller.setSchema(schema);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log log = (Log) jaxbUnmarshaller.unmarshal(file);
            records = log.getRecords();

            for (Record record : records) {
                record.setTimestamp(record.getTimestamp() * 1000);
                record.setSession(record.getSession() * 1000);
            }
        } catch (UnmarshalException e) {
            throw e;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     *
     * @param map Map which contains list of outputSchema records (value) for each day (key)
     * @param file XML-file the map is written to
     */
    public static void writeXmlRecords(Map<String, List<OutputRecord>> map, File file) {
        OutputLog log = new OutputLog();
        List<DailyRecord> dailyRecords = new ArrayList<DailyRecord>();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(OutputLog.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            for (Map.Entry<String, List<OutputRecord>> pair : map.entrySet()) {
                DailyRecord dailyRecord = new DailyRecord(pair.getKey().toUpperCase());
                List<OutputRecord> outputRecords = new ArrayList<OutputRecord>();

                // sort by userID first
                Collections.sort(pair.getValue());
                for (OutputRecord record : pair.getValue()) {
                    outputRecords.add(record);
                }
                dailyRecord.setRecords(outputRecords);
                dailyRecords.add(dailyRecord);
            }
            log.setDailyRecords(dailyRecords);
            jaxbMarshaller.marshal(log, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
