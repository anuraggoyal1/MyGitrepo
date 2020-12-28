package com.anurag.stocks.volume.analysis;

import com.anurag.stocks.model.NSEFullData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BiWeeklyPriceVolumeProcessor {

  static String inputPath = "/Users/a0g073t/Documents/daily_data/input-nse-full/";
  static String outputPath = "/Users/a0g073t/Documents/daily_data/output-nse-biweekly-up-data/";


  static String out_header =
      "symbol,current_price,weeks%UP, weeksToPreviousDaysDeliveryVolRatio" + "\n";
  static StringBuilder csvOutData = new StringBuilder(out_header);

  public static void main(String[] args) throws IOException {

    int numberOfTradingDaysInWeek = 10;
    // read last 5 days files
    String[] files = loadFiles(inputPath, 30);
    List<Map<String, NSEFullData>> inputFileData = new LinkedList<>(); // Map of a day. and kept
                                                                       // inside a list.
    int index = 0;
    for (String file : files) {
      inputFileData.add(index, readFile(file));
      index++;
    }

    StringBuffer outputFileData =
        generateOutputForLatestFile(inputFileData, numberOfTradingDaysInWeek);

    saveDataToOutPath(files[0], outputFileData);

    System.out.println(files.length);

  }


  private static StringBuffer generateOutputForLatestFile(
      List<Map<String, NSEFullData>> inputFileData, int numberOfTradingDaysInWeek) {
    Map<String, NSEFullData> weekEnd = inputFileData.get(0); //latest file (zeroth)
    Map<String, NSEFullData> weekStart = inputFileData.get(numberOfTradingDaysInWeek - 1); //10 days ago file

    Map<String, NSEFullData> oldest = inputFileData.get(inputFileData.size() - 1); //30th file

    StringBuffer st = new StringBuffer();
    int counter = 0;
    for (String s : oldest.keySet()) {
      // System.out.println(s);

      if (weekStart.get(s) != null && weekEnd.get(s) != null
          && weekStart.get(s).getSeries().trim().equals("EQ") && weekEnd.get(s).getClose() > 25) {
        float weeksPriceChange =
            calculatePriceChange(weekStart.get(s).getPrev_close(), weekEnd.get(s).getClose());

        int fromIndex = numberOfTradingDaysInWeek;  //always last 10 days
        int toIndex = inputFileData.size();

        float previousAvgDevVol =
            calculatePreAvgDelVol(s, inputFileData.subList(fromIndex, toIndex));

        fromIndex = 0;
        toIndex = numberOfTradingDaysInWeek; // in sublist, this is exclusive

        float weeksAvgDevVol = calculatePreAvgDelVol(s, inputFileData.subList(fromIndex, toIndex));

        float volRatio = weeksAvgDevVol / previousAvgDevVol;
                            
  //      if (weeksPriceChange > 5.9 && volRatio > 1.21) {
          st.append(s).append(".NS,").append(weekEnd.get(s).getClose()).append(",")
              .append(weeksPriceChange).append(",").append(volRatio).append("\n");

          counter++;
        }
      }
  //  }
    System.out.println("total - " + counter);
    return st;
  }

  private static float calculatePreAvgDelVol(String s,
      List<Map<String, NSEFullData>> inputFileData) {
    int count = 0;
    float vol = 0;
    for (Map<String, NSEFullData> d : inputFileData) {
      if (d.get(s) != null) {
        vol = vol + d.get(s).getDel_qty();
        count++;
      }
    }
    return vol / count;
  }


  private static void saveDataToOutPath(String fileName, StringBuffer outputFileData) {
    try {
      saveToFile(fileName, outputFileData);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // System.out.println(outputFileData);
  }

  public static void saveToFile(String fileName, StringBuffer outputFileData) throws IOException {
    fileName = "new_" + fileName;
    if ((new File(outputPath + fileName).exists())) {
      new File(outputPath + fileName).delete();

    }
    new File(outputPath + fileName).createNewFile();
    Path writePath = Files.write(Paths.get(outputPath + fileName),
        outputFileData.toString().getBytes(), StandardOpenOption.WRITE);
    System.out.println("saved file at- " + writePath);
  }



  private static float calculatePriceChange(float previousClose, float close) {
    return ((close - previousClose) / previousClose) * 100;
  }

  private static Map<String, NSEFullData> readFile(String file) {
    String csvFile = inputPath + file;
    System.out.println(" path is -" + csvFile);

    Map<String, NSEFullData> allFilesMap = new HashMap<String, NSEFullData>();
    NSEFullData s = null;
    CSVReader reader = null;
    String[] line = null;
    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

      while ((line = reader.readNext()) != null && line[0] != "") {

        if (line[1].replaceAll(" ", "").equals("EQ") || line[1].replaceAll(" ", "").equals("BE")) {
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

  public static String[] loadFiles(String path, int count) {
    String[] files = new String[count];
    int i = 0;
    // latest file is at zeroth index.
    LocalDate dt1 = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
    while (true) {
      String fName = "sec_bhavdata_full_" + formatter.print(dt1) + ".csv";
      if (new File(path + fName).exists()) {
        files[i] = fName;
        count--;
        i++;
        if (count == 0)
          break;
      }
      dt1 = dt1.minusDays(1);
    }
    return files;
  }
}

