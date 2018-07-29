package com.anurag.stocks;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.anurag.stocks.model.NSEFullData;
import com.anurag.stocks.model.StockOHLCData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;


public class GenerateOHLCAndEMAFromNSEBhavCopy {
  static int j = 0;

  static String header =
      "symbol,date,open,high,low,close,ema3d,ema5d,ema10d,ema20d,ema30d,ema50d,ema100d,ema150d,ema200d"
          + "\n";

  static String path = "/Users/anuraggoyal/Documents/daily_data/input-nse-bhav/";
  static String outpath = "/Users/anuraggoyal/Documents/daily_data/output-nse-bhav_ema/";

  static DecimalFormat df = new DecimalFormat("#.00");
  static float ema3 = 0.0f, ema5 = 0.0f, ema10 = 0.0f, ema20 = 0.0f, ema30 = 0.0f, ema50 = 0.0f,
      ema100 = 0.0f, ema150 = 0.0f, ema200 = 0.0f;

  static float O = 0.0f, H = 0.0f, L = 0.0f, C = 0.0f;

  static int latestIdx = 0;
  static String latestDate = "";

  static Set<String> dates = new HashSet<String>();

  static Map<String, StockOHLCData> symbolOnDate = new HashMap<String, StockOHLCData>();
  static List<Map<String, NSEFullData>> inputFileData = new LinkedList<>(); // Map of a day. and
                                                                            // kept

  public static void main(String[] args) throws IOException {

    String[] fileNames = loadFiles(path);



    // inside a list.
    int index = 0;
    for (String file : fileNames) {
      inputFileData.add(index, readFile(file));
      index++;
    }
    System.out.println("loaded all files." + inputFileData.size());

    latestIdx = inputFileData.size() - 1;
    StringBuilder ohlcAndEMAData = getOhlcAndEMAData();
    saveToFile(latestDate, ohlcAndEMAData);
  }

  private static Map<String, NSEFullData> readFile(String file) {
    String csvFile = path + file;
    System.out.println(" path is -" + csvFile);

    Map<String, NSEFullData> allFilesMap = new HashMap<String, NSEFullData>();
    NSEFullData s = null;
    CSVReader reader = null;
    String[] line = null;
    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

      while ((line = reader.readNext()) != null) {

        if (line[1].replaceAll(" ", "").equals("EQ") || line[1].replaceAll(" ", "").equals("BE")) {
          s = new NSEFullData();
          s.setSymbol(line[0]);
          s.setSeries(line[1]);
          s.setOpen(Float.parseFloat(line[2]));
          s.setHigh(Float.parseFloat(line[3]));
          s.setLow(Float.parseFloat(line[4]));
          s.setClose(Float.parseFloat(line[5]));
          s.setPrev_close(Float.parseFloat(line[7]));
          s.setDate(line[10]);
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

  public static StringBuilder getOhlcAndEMAData() {

    System.out.println("calculating master data....");

    StringBuilder csvData = new StringBuilder(header);

    List<String> symbols = Symbols.getSymbols();

    for (String s : symbols) {
      System.out.println(s);

      O = inputFileData.get(latestIdx).get(s).getOpen();
      H = inputFileData.get(latestIdx).get(s).getHigh();
      L = inputFileData.get(latestIdx).get(s).getLow();
      C = inputFileData.get(latestIdx).get(s).getClose();

      ema3 = calculateLatestEMA(s, 3);
      ema5 = calculateLatestEMA(s, 5);
      ema10 = calculateLatestEMA(s, 10);
      ema20 = calculateLatestEMA(s, 20);
      ema30 = calculateLatestEMA(s, 30);
      ema50 = calculateLatestEMA(s, 50);
      ema100 = calculateLatestEMA(s, 100);
      ema150 = calculateLatestEMA(s, 150);
      ema200 = calculateLatestEMA(s, 200);

      String date = inputFileData.get(latestIdx).get(s).getDate();
      latestDate = date;
      csvData.append(s).append(",").append(date).append(",").append(O).append(",").append(H)
          .append(",").append(L).append(",").append(C).append(",").append(ema3).append(",")
          .append(ema5).append(",").append(ema10).append(",").append(ema20).append(",")
          .append(ema30).append(",").append(ema50).append(",").append(ema100).append(",")
          .append(ema150).append(",").append(ema200).append("\n");

    }

    return csvData;
  }



  public static float calculateLatestEMA(String sym, int N) {
    float emaYesterday = 0;
    float emaToday = 0;
    float K = 2.0f / (N + 1.0f);
    float pToday = 0.0f;
    int counter = 0;

    for (Map<String, NSEFullData> date : inputFileData) {
      if (date.get(sym) == null)
        continue;

      emaYesterday = emaToday;
      pToday = date.get(sym).getClose();
      emaToday = Float.parseFloat(df.format((pToday * K) + (emaYesterday * (1 - K))));
      counter++;
      System.out.println(sym+ " "+counter + ". "+emaYesterday +" :: "+pToday+" :: "+emaToday);
    }

    if (counter < N) {
      // not enough days data available to calculate N ema.
      System.out.println(sym+" -not enough days data ("+ counter +") available to calculate " + N + " days ema");
      return 0.0f;
    }
   
    return emaToday;
  }

  public static void saveToFile(String date, StringBuilder csvData) throws IOException {
    if ((new File(outpath + "ema_price_" + date + ".csv").exists())) {
      new File(outpath + "ema_price_" + date + ".csv").delete();
    }

    new File(outpath + "ema_price_" + date + ".csv").createNewFile();

    Files.write(Paths.get(outpath + "ema_price_" + date + ".csv"), csvData.toString().getBytes(),
        StandardOpenOption.WRITE);
  }

  public static String[] loadFiles(String path) {

    File dir = new File(path);
    int count = dir.list().length;
    int i = 0;
    System.out.println("total files are - " + count);

    String[] files = new String[count-1];

    Calendar c = Calendar.getInstance();
    c.set(2017, 0, 1);

    System.out.println("start date is -" + c.getTime());
    LocalDate startDate = LocalDate.fromCalendarFields(c);

    DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMMyyyy");

    while (true) {
      String fName = "cm" + formatter.print(startDate).toUpperCase() + "bhav.csv";
      if (new File(path + fName).exists()) {
        files[i] = fName;
        i++;
      }
        if (i == count-1)
          break;
      
      startDate = startDate.plusDays(1);
    }



    return files;

  }
}
