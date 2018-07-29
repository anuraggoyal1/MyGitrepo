package com.anurag.stocks.volume.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

//needs review. its wrong
public class NSEDataAnalyzer {

  static String inputPath = "/Users/anuraggoyal/Documents/daily_data/output/";
  static String outputPath = "/Users/anuraggoyal/Documents/daily_data/analysed/";
  static String header =
      "symbol,price,avgVolumeChange%, volFlagCount,changeInPrice%,delivery%,upDownVolRatio " + "\n";
  static StringBuilder csvOutData = new StringBuilder(header);

  // load last n files
  // for each script, check last n files and sum their volFlag and average the delivery volume

  public static void main(String[] args) throws IOException {

    int count = 20;

    // read last 50 days files
    String[] files = loadFiles(inputPath, count);
    List<Map<String, NseFullOutData>> inputFileData = new LinkedList<>();
    int index = 0;
    for (String file : files) {
      inputFileData.add(index, readFile(file));
      index++;
    }

    System.out.println("Earliest file is - " + files[0]);
    System.out.println("Last file is - " + files[files.length - 1]);

    Map<String, NseFullOutData> latest = inputFileData.get(0);
    for (String s : latest.keySet()) {

      float price = inputFileData.get(0).get(s).getClose();
      float changeInPrice = calculateChangeInPrice(s, inputFileData);
      float sumOfVolFlag = calculateSumOfVolFlag(s, inputFileData);

      if (price > 20) {
        csvOutData.append(s).append(",").append(price).append(",")
            .append(calculateAvgVolumeChange(s, inputFileData)).append(",").append(sumOfVolFlag)
            .append(",").append(changeInPrice).append(",").append("").append(",")
            .append(calculateUpDownVolRatio(s, inputFileData)).append("\n");
      }
    }


    saveToFile(new LocalDate() + "_count-" + count + "_.csv", csvOutData);


  }

  private static float calculateUpDownVolRatio(String s,
      List<Map<String, NseFullOutData>> inputFileData) {
    float upVol = 0.0f, downVol = 0.0f;
    for (int i = 0; i < inputFileData.size(); i++) {
      if (inputFileData.get(i).get(s) != null) {
        if (inputFileData.get(i).get(s).getClose() > inputFileData.get(i).get(s).getOpen()) {
          upVol = upVol + inputFileData.get(i).get(s).getToday_delivery_qty();
        } else {
          downVol = downVol + inputFileData.get(i).get(s).getToday_delivery_qty();
        }
      }
    }
    return upVol / downVol;
  }

  public static void saveToFile(String fileName, StringBuilder csvData) throws IOException {
    fileName = "analysed_" + fileName;
    if (!(new File(outputPath + fileName).exists())) {
      new File(outputPath + fileName).delete();

    }
    new File(outputPath + fileName).createNewFile();
    Files.write(Paths.get(outputPath + fileName), csvData.toString().getBytes(),
        StandardOpenOption.WRITE);

  }

  private static float calculateChangeInPrice(String s,
      List<Map<String, NseFullOutData>> inputFileData) {
    float latestPrice = inputFileData.get(0).get(s).getClose();

    float oldestPrice = 0.0f;
    if (inputFileData.get(inputFileData.size() - 1).get(s) != null) // some cases a symbol is not
                                                                    // present in file
      oldestPrice = inputFileData.get(inputFileData.size() - 1).get(s).getClose();


    return ((latestPrice - oldestPrice) / oldestPrice) * 100;


    // return 0.0f;
  }

  private static float calculateSumOfVolFlag(String s,
      List<Map<String, NseFullOutData>> inputFileData) {
    int volFlag = 0;
    for (int i = 0; i < inputFileData.size(); i++) {
      if (inputFileData.get(i).get(s) != null) {
  //      volFlag = volFlag + inputFileData.get(i).get(s).getIsVolMoreThanAvg();
      }
    }
    return volFlag;
  }


  private static float calculateAvgVolumeChange(String s,
      List<Map<String, NseFullOutData>> inputFileData) {
    float divider = inputFileData.size();
    float vol = 0;
    for (int i = 0; i < inputFileData.size(); i++) {
      if (inputFileData.get(i).get(s) != null) {
        vol = vol + inputFileData.get(i).get(s).getToday_delivery_qty();
      } else {
        divider = divider - 1; // reduce divider by 1 when that day volume is not available.
      }
    }
    return vol / divider;
  }


  private static Map<String, NseFullOutData> readFile(String file) {
    String csvFile = inputPath + file;
    // System.out.println(" path is -" + csvFile);

    Map<String, NseFullOutData> allFilesMap = new HashMap<String, NseFullOutData>();
    NseFullOutData s = null;
    CSVReader reader = null;
    String[] line = null;
    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

      while ((line = reader.readNext()) != null && line[0] != "") {
        s = new NseFullOutData();
        s.setSymbol(line[0]);
        s.setOpen(Float.parseFloat(line[1]));
        s.setHigh(Float.parseFloat(line[2]));
        s.setLow(Float.parseFloat(line[3]));
        s.setClose(Float.parseFloat(line[4]));
        s.setPriceChange(Float.parseFloat(line[5]));
 //       s.setToday_delivery_qty(Long.parseLong(line[6]));
 //       s.setVolume3mAvg(Long.parseLong(line[7]));
 //       s.setVolumeChange(Float.parseFloat(line[8]));
 //       s.setIsVolMoreThanAvg(Integer.parseInt(line[9]));
        // s.setDeliveryPercent(Float.parseFloat(line[10]));

        allFilesMap.put(line[0], s);
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

    LocalDate dt1 = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMMYYY");
    while (true) {
      String fName = "new_cm" + formatter.print(dt1).toUpperCase() + "bhav.csv";
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
