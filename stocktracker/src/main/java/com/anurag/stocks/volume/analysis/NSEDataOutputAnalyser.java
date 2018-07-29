package com.anurag.stocks.volume.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class NSEDataOutputAnalyser {

  static String inputPath = "/Users/anuraggoyal/Documents/daily_data/output-nse-full/";
  static String outputPath = "/Users/anuraggoyal/Documents/daily_data/output-nse-analysis/";
  static String latestDate;

  static StringBuilder csvOutData = new StringBuilder();

  public static void main(String[] args) throws IOException {
    // read last 50 days files
    Map<String, String> files = loadFiles(inputPath, 17);
    Map<String, List<NseFullOutData>> inputFileData = new LinkedHashMap(); // Map of a day. and kept
                                                             // inside a list.
    ArrayList<String> filesList = new ArrayList<>(files.keySet());
    Collections.reverse(filesList);
    
    for (String date : filesList) {
      inputFileData.put(date, readFile(date));

    }

    Map<String, StringBuilder> outputFileData = generateOutputData(inputFileData);
    ArrayList<String> arrayList = new ArrayList<>(outputFileData.keySet());

    Collections.sort(arrayList);
    for (String s : arrayList) {
    //  System.out.println(s + "," + outputFileData.get(s));
      csvOutData.append(s).append(",").append(outputFileData.get(s).append("\n"));
    }

    saveToFile(latestDate, csvOutData);

  }



  public static void saveToFile(String date, StringBuilder csvData) throws IOException {
    String fileName = "analysis_" + date + ".csv";
    if ((new File(outputPath + fileName).exists())) {
      new File(outputPath + fileName).delete();

    }
    new File(outputPath + fileName).createNewFile();
    Path writePath = Files.write(Paths.get(outputPath + fileName), csvData.toString().getBytes(),
        StandardOpenOption.WRITE);
    System.out.println("saved file at- " + writePath);
  }

  private static Map<String, StringBuilder> generateOutputData(
      Map<String, List<NseFullOutData>> inputFileData) {
    Map<String, StringBuilder> outputFileData = new HashMap<>();


    for (String date : inputFileData.keySet()) {
      List<NseFullOutData> symbolsOnDate = inputFileData.get(date);
      for (NseFullOutData s : symbolsOnDate) {
        if (outputFileData.containsKey(s.getSymbol())) {
          outputFileData.put(s.getSymbol(), outputFileData.get(s.getSymbol()).append(",").append(date).append("|").append(s.getDeliveryVolChangeInPercent()));
        } else {
          outputFileData.put(s.getSymbol(), new StringBuilder(date).append("|").append(s.getDeliveryVolChangeInPercent()));
        }

      }

    }

    return outputFileData;


  }



  private static List<NseFullOutData> readFile(String date) {
    String csvFile = inputPath + "new_sec_bhavdata_full_" + date + ".csv";
    System.out.println(" path is -" + csvFile);

    List<NseFullOutData> symbols = new LinkedList<>();


    CSVReader reader = null;
    String[] line = null;
    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

      while ((line = reader.readNext()) != null) {
        NseFullOutData nfo = new NseFullOutData();
        nfo.setSymbol(line[0]);
        nfo.setDeliveryVolChangeInPercent(Float.parseFloat(line[9]));
        symbols.add(nfo);
      }

    } catch (IOException e) {
      System.out.println("error on loading day-" + csvFile);
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("error on loading day-" + csvFile + " with symbol-" + line[0]);
      e.printStackTrace();
    }
    return symbols;
  }

  public static Map<String, String> loadFiles(String path, int count) {
    Map<String, String> file = new LinkedHashMap<>();
    boolean latestDateFound = false;
    LocalDate dt1 = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
    while (true) {
      String fName = "new_sec_bhavdata_full_" + formatter.print(dt1) + ".csv";
      if (new File(path + fName).exists()) {
        file.put(formatter.print(dt1), fName);
        if (!latestDateFound) {
          latestDate = formatter.print(dt1);
          latestDateFound = true;
        }

        count--;

        if (count == 0)
          break;
      }
      dt1 = dt1.minusDays(1);
    }
    return file;
  }
}

