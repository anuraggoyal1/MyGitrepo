package com.anurag.stocks.jan2021;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.NSEFullData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;


/**
 * compare last two weeks data from last previous 4 weeks
 */
public class NSEDataInputXWeekToYWeekPriceProcessor {

  static String inputPath = "/Users/a0g073t/Documents/daily_data/input-nse-full/";
  static String outputPath = "/Users/a0g073t/Documents/daily_data/output-nse-last4weeks-up-data/";


  static String out_header =
      "symbol,current_price, previous 4 weeks change%, current 2 weeks change%, current delivery volume, volume ratio" + "\n";
  static StringBuilder csvOutData = new StringBuilder(out_header);


  static int inputPeriodInWeeks = 2;
  static int compareWithPeriodInWeeks = 6;

  static int delVolumeForInputPeriod = 100000 ;

  public static void main(String[] args) throws IOException {

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String startDate = "17-01-2021"; //always give sunday date
    LocalDate inputDate = LocalDate.from(dateFormatter.parse(startDate));



    // read last 5 days files
    List<Map<String, NSEFullData>> currentPeriod = loadFiles(inputPath, inputDate, inputPeriodInWeeks*7);// Map of a day. and kept
    // inside a list.

    System.out.println(currentPeriod.size());


    List<Map<String, NSEFullData>>  previousPeriod = loadFiles(inputPath, inputDate.minusDays(inputPeriodInWeeks*7), inputPeriodInWeeks*7*compareWithPeriodInWeeks);// Map of a day. and kept

    System.out.println(previousPeriod.size());


    StringBuffer outputFileData =
        generateOutputForLatestFile(currentPeriod, previousPeriod);

    String monthValue = "";
    if(inputDate.getMonthValue() < 10)
      monthValue = "0"+inputDate.getMonthValue();
    else
      monthValue = String.valueOf(inputDate.getMonthValue());

    saveDataToOutPath("weekly-"+startDate+"_"+inputPeriodInWeeks+"_weeks_to_"+compareWithPeriodInWeeks+"_weeks"+".csv", monthValue+"-"+inputDate.getYear(), outputFileData);


  }


  private static StringBuffer generateOutputForLatestFile(
      List<Map<String, NSEFullData>> current, List<Map<String, NSEFullData>> previous) {

    Symbols sym = new Symbols();
    Set<String> badStocks = sym.getBadStocksSymbols();


    Map<String, NSEFullData> currentlatest = current.get(0);
    Map<String, NSEFullData> currentOldest = current.get(current.size() - 1);

    Map<String, NSEFullData> previouslatest = previous.get(0);
    Map<String, NSEFullData> previousOldest = previous.get(previous.size() - 1);



    StringBuffer st = new StringBuffer();
    st.append(out_header);
    int counter = 0;
    for (String s : currentOldest.keySet()) {
      // System.out.println(s);

      if (currentlatest.get(s) != null && currentOldest.get(s) != null && previouslatest.get(s) != null && previousOldest.get(s) != null) {
        float currentPriceChange =
            calculatePriceChange(currentOldest.get(s).getPrev_close(), currentlatest.get(s).getClose());

        float currentDeliveryVolume = calculateTotalDelVol(s, current);


        float previousPriceChange =
                calculatePriceChange(previousOldest.get(s).getPrev_close(), previouslatest.get(s).getClose());

        float previousAvgDeliveryVolume = calculateTotalDelVol(s, previous) / compareWithPeriodInWeeks;  // current 4 week to last 8 week avg

        float volRatio = currentDeliveryVolume / previousAvgDeliveryVolume;
                            

        if(currentOldest.get(s).getPrev_close() > 30 && currentDeliveryVolume > delVolumeForInputPeriod && currentPriceChange > 4 && volRatio> 2 && !badStocks.contains(s)) {
          st.append(s).append(".NS,").append(currentlatest.get(s).getClose()).append(",").append(previousPriceChange).append(",")
                  .append(currentPriceChange).append(",").append(currentDeliveryVolume).append(",").append(volRatio).append("\n");
          counter++;
        }

        }
      }

    System.out.println("total - " + counter);
    return st;
  }

  private static float calculateTotalDelVol(String s,
                                            List<Map<String, NSEFullData>> inputFileData) {

    float vol = 0;
    for (Map<String, NSEFullData> d : inputFileData) {
      if (d.get(s) != null) {
        vol = vol + d.get(s).getDel_qty();
      }
    }
    return vol;
  }


  private static void saveDataToOutPath(String fileName, String folderName, StringBuffer outputFileData) {
    try {
      saveToFile(fileName, folderName, outputFileData);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // System.out.println(outputFileData);
  }

  public static void saveToFile(String fileName, String folderName, StringBuffer outputFileData) throws IOException {

    if(!new File(outputPath + folderName ).exists()){
      new File(outputPath + folderName).mkdir();
    }

    if ((new File(outputPath + folderName +"/"+fileName).exists())) {
      new File(outputPath + folderName +"/"+fileName).delete();

    }
    new File(outputPath + folderName +"/"+fileName).createNewFile();
    Path writePath = Files.write(Paths.get(outputPath + folderName +"/"+fileName),
        outputFileData.toString().getBytes(), StandardOpenOption.WRITE);
    System.out.println("saved file at- " + writePath);
  }



  private static float calculatePriceChange(float previousClose, float close) {
    return ((close - previousClose) / previousClose) * 100;
  }

  private static Map<String, NSEFullData> readFile(String file, String folderName) {
    String csvFile = inputPath+ folderName + file;
    System.out.println(" path is -" + csvFile);

    Map<String, NSEFullData> allFilesMap = new HashMap<String, NSEFullData>();
    NSEFullData s = null;
    CSVReader reader = null;
    String[] line = null;
    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

      while ((line = reader.readNext()) != null && line[0] != "") {

        if (line[1].replaceAll(" ", "").equals("EQ")) {
          s = new NSEFullData();
          s.setSymbol(line[0]);
          s.setSeries(line[1]);
          s.setDate(line[2]);

          s.setPrev_close(Float.parseFloat(line[3]));
          s.setOpen(Float.parseFloat(line[4]));
          s.setHigh(Float.parseFloat(line[5]));
          s.setLow(Float.parseFloat(line[6]));
          s.setClose(Float.parseFloat(line[8]));

          s.setTol_trd_qty(Long.parseLong(line[10].replaceAll(" ", "")));
          s.setDel_qty(Long.parseLong(line[13].replaceAll(" ", "").replaceAll("-", "0")));
          s.setDel_percent(Float.parseFloat(line[14].replaceAll(" ", "").replaceAll("-", "0")));

          allFilesMap.put(line[0], s);
        }
      }
    } catch (IOException e) {
      System.out.println("error on loading day-" + file);
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("error on loading day-" + file + " with symbol-" + line[0]);
      e.printStackTrace();
    }
    return allFilesMap;
  }

  public static List<Map<String, NSEFullData>> loadFiles(String path, LocalDate startDate, int count) {

    List<Map<String, NSEFullData>> inputFileData = new LinkedList<>();
    String monthValue = "";
    // latest file is at zeroth index.
    org.joda.time.LocalDate dt1 = org.joda.time.LocalDate.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    while (true) {
      String fName = "sec_bhavdata_full_" + startDate.format(dateFormatter) + ".csv";

      if(startDate.getMonthValue() < 10)
        monthValue = "0"+startDate.getMonthValue();
      else
        monthValue = String.valueOf(startDate.getMonthValue());


      if (new File(path + monthValue+"-"+startDate.getYear() +"/" +fName).exists()) {
        inputFileData.add(readFile(fName, monthValue + "-" + startDate.getYear() + "/"));
      }
      count--;

      if (count == 0)
          break;

      startDate = startDate.minusDays(1);
    }
    return inputFileData;
  }
}

