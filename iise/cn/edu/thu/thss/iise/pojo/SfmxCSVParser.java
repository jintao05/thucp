package cn.edu.thu.thss.iise.pojo;
/**
 *
 */

/**
 * @author Tao Jin
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
public class SfmxCSVParser {

	/*
	 * read sfmx items from csv file with format of utf-8, and the date has the format as yyyy-mm-dd
	 */
	public static List<Sfmx> readSfmxs(String fileName) throws IOException, ParseException {

        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

        //initialize the CSVParser object
        CSVParser parser = CSVParser.parse(new File(fileName), StandardCharsets.UTF_8, format);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");

        List<Sfmx> sfmxs = new ArrayList<Sfmx>();
        for(CSVRecord record : parser){
            Sfmx sfmx = new Sfmx();
            sfmx.setGrbm(record.get("GRBM"));
            sfmx.setMc(record.get("F_MC"));
            sfmx.setDl(record.get("DL"));
//            sfmx.setDj(Float.parseFloat(record.get("F_DJ")));
//            sfmx.setZl(Float.parseFloat(record.get("F_ZL")));
//            sfmx.setZje(Float.parseFloat(record.get("F_ZJE")));
//            String date = df.format(record.get("F_RQ"));
//            sfmx.setRq(df.parse(record.get("F_RQ")));
//            sfmx.setRq(df2.parse(record.get("F_RQ")));
            sfmx.setRq(df.parse(record.get("F_RQ")));
            sfmxs.add(sfmx);
        }
        //close the parser
        parser.close();

        return sfmxs;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{

	    //Create the CSVFormat object
        List<Sfmx> sfmxs = SfmxCSVParser.readSfmxs("c:/test/forfrauddetection.csv");

        System.out.println(sfmxs.size());
        System.out.println(sfmxs.get(0));
	}
}
