package com.anurag.stocks.volume.analysis;

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
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.NSEFullData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class NSEDataInputProcessor {

  static String inputPath = "/Users/anuraggoyal/Documents/daily_data/input-nse-full/";
  static String outputPath = "/Users/anuraggoyal/Documents/daily_data/output-nse-full/";


  static String out_header =
      "symbol,open,high,low,close,price_Change_%,total_TradedQty,average_DeliveryQty,today_Delivery_Qty,delivery_qty_change_%,net_delivery_%,up_down_del_vol_ratio"
          + "\n";
  static StringBuilder csvOutData = new StringBuilder(out_header);

  public static void main(String[] args) throws IOException {
    // read last 50 days files
    String[] files = loadFiles(inputPath, 38);
    List<Map<String, NSEFullData>> inputFileData = new LinkedList<>(); // Map of a day. and kept
                                                                       // inside a list.
    int index = 0;
    for (String file : files) {
      inputFileData.add(index, readFile(file));
      index++;
    }

    Map<String, NseFullOutData> outputFileData = generateOutputForLatestFile(inputFileData);

    saveDataToOutPath(files[0], outputFileData);

    System.out.println(files.length);

 //   checkLastTenDaysGrowingStocks(inputFileData, Symbols.getSymbols());

  }

  private static void checkLastTenDaysGrowingStocks(List<Map<String, NSEFullData>> inputFileData,
      List<String> symbols) {
    System.out.println("stocks growing for last 10 days .....\n");
    int priceIncreaseCounter = 0;
    float deliveryAverage = 0;
    for (String sym : Symbols.getSymbols()) {
      for (int i = 0; i < 10; i++) {
        NSEFullData sdata = inputFileData.get(i).get(sym);
      // System.out.println(sym);
        if (calculatePriceChange(sdata.getOpen(), sdata.getClose()) > 0) {
          priceIncreaseCounter++;
        }
        deliveryAverage=deliveryAverage+sdata.getDel_percent();
      }
      if (priceIncreaseCounter >= 7) {
        float changeInPriceInLastTenDays = calculatePriceChange(
            inputFileData.get(9).get(sym).getOpen(), inputFileData.get(0).get(sym).getClose());
        if (changeInPriceInLastTenDays > 4) {
          System.out.println(sym + "  price:"+inputFileData.get(0).get(sym).getClose() +" change: " + changeInPriceInLastTenDays + " % counter - "+priceIncreaseCounter + "  delivery: "+deliveryAverage/10 );
        }
      }
      priceIncreaseCounter = 0;
      deliveryAverage=0;
    }

  }

  private static void saveDataToOutPath(String fileName,
      Map<String, NseFullOutData> outputFileData) {

    try {
      NseFullOutData s = null;
      int count = 0;
      for (String sym : Symbols.getSymbols()) {
        s = outputFileData.get(sym);

        if (s != null && s.getClose() > 15 && s.getPriceChange() > 0 && s.getDeliveryVolChangeInPercent() >300) {

          csvOutData.append(s.getSymbol()).append(",").append(s.getOpen()).append(",")
              .append(s.getHigh()).append(",").append(s.getLow()).append(",").append(s.getClose())
              .append(",").append(s.getPriceChange()).append(",").append(s.getToday_traded_qty())
              .append(",").append(s.getAvg_delivery_qty()).append(",")
              .append(s.getToday_delivery_qty()).append(",")
              .append(s.getDeliveryVolChangeInPercent()).append(",")
              .append(s.getNetDeliveryToTradedVolInPercent()).append(",")
              .append(s.getUp_down_del_vol_ratio()).append("\n");
          count++;
        }
      }
      System.out.println("total - " + count);

      saveToFile(fileName, csvOutData);

      // just for printing on console
      csvOutData = new StringBuilder();
      System.out.println("\n\n");
      for (String sym : Symbols.getSymbols()) {
        s = outputFileData.get(sym);

        if (s != null && s.getClose() > 15 && s.getPriceChange() > 0 && s.getDeliveryVolChangeInPercent() > 300) {

          csvOutData.append(s.getSymbol()).append("     ").append(s.getClose()).append("      ")
              .append(String.format("%.2f", s.getPriceChange())).append("%      ")
              .append(s.getToday_delivery_qty() / 1000).append("K      ")
              .append(s.getDeliveryVolChangeInPercent()).append("%      ")
              .append(s.getNetDeliveryToTradedVolInPercent()).append("%      ").append("\n\n");
          count++;
        }
      }

      System.out.println(csvOutData);

      // end console printing
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }

  public static void saveToFile(String fileName, StringBuilder csvData) throws IOException {
    fileName = "new_" + fileName;
    if ((new File(outputPath + fileName).exists())) {
      new File(outputPath + fileName).delete();

    }
    new File(outputPath + fileName).createNewFile();
    Path writePath = Files.write(Paths.get(outputPath + fileName), csvData.toString().getBytes(),
        StandardOpenOption.WRITE);
    System.out.println("saved file at- " + writePath);
  }

  private static Map<String, NseFullOutData> generateOutputForLatestFile(
      List<Map<String, NSEFullData>> inputFileData) {
    Map<String, NseFullOutData> outputFileData = new HashMap<String, NseFullOutData>();

    Map<String, NSEFullData> latest = inputFileData.get(0);
    for (String s : latest.keySet()) {

      NseFullOutData outData = new NseFullOutData();
      outData.setSymbol(s);
      outData.setDate(latest.get(s).getDate());
      outData.setOpen(latest.get(s).getOpen());
      outData.setHigh(latest.get(s).getHigh());
      outData.setLow(latest.get(s).getLow());
      outData.setClose(latest.get(s).getClose());

      outData.setToday_traded_qty(latest.get(s).getTol_trd_qty());
      outData.setToday_delivery_qty(latest.get(s).getDel_qty());
      outData.setNetDeliveryToTradedVolInPercent(latest.get(s).getDel_percent());
      outData.setPriceChange(
          calculatePriceChange(latest.get(s).getPrev_close(), (latest.get(s).getClose())));
      outData.setAvg_delivery_qty(calculateAvgUPDaysDelVolume(s, inputFileData));
      outData.setDeliveryVolChangeInPercent(
          calculateVolumeChange(outData.getAvg_delivery_qty(), latest.get(s).getDel_qty()));

      outData.setUp_down_del_vol_ratio(calculateUpDownVolRatio(inputFileData, s));

      outputFileData.put(s, outData);

    }
    return outputFileData;
  }



  private static float calculateUpDownVolRatio(List<Map<String, NSEFullData>> inputFileData,
      String sym) {
    float accumulationVolume = 0;
    float distributionVolume = 0;
    float priceChange = 0.0f;
    for (Map<String, NSEFullData> map : inputFileData) {
      if (map.get(sym) != null) {
        priceChange = calculatePriceChange(map.get(sym).getPrev_close(), map.get(sym).getClose());

        if (priceChange > 0)
          accumulationVolume = accumulationVolume + map.get(sym).getDel_qty();
        else
          distributionVolume = distributionVolume + map.get(sym).getDel_qty();
      }

    }


    return accumulationVolume / distributionVolume;
  }

  private static float calculateVolumeChange(long volume3mAvg, long volume) {
    return ((float) volume / (float) volume3mAvg) * 100;
  }

  private static long calculateAvgUPDaysDelVolume(String s,
      List<Map<String, NSEFullData>> inputFileData) {
    long divider = 0;
    long vol = 0;
    for (int i = 1; i < inputFileData.size(); i++) {

      if (inputFileData.get(i).get(s) != null
          && calculatePriceChange(inputFileData.get(i).get(s).getPrev_close(),
              inputFileData.get(i).get(s).getClose()) > 0) {
        vol = vol + inputFileData.get(i).get(s).getDel_qty();
        divider++;
      }
    }
    if (divider == 0)
      return 0;
    return vol / divider;
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

