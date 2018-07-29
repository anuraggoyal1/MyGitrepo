package com.anurag.stocks;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.anurag.stocks.model.StockOHLCData;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class GenerateHistoricalOHLCAndEMAFromYahoo {
  static int j = 0;
  static String header =
      "symbol,date,open,high,low,close,ema3d,ema5d,ema10d,ema20d,ema30d,ema50d,ema100d,ema150d,ema200d"
          + "\n";
  static String path = "/Users/anuraggoyal/Documents/daily_tracker/bulk/daily/";
  static float ema3 = 0.0f, ema5 = 0.0f, ema10 = 0.0f, ema20 = 0.0f, ema30 = 0.0f, ema50 = 0.0f,
      ema100 = 0.0f, ema150 = 0.0f, ema200 = 0.0f;
  static float O = 0.0f, H = 0.0f, L = 0.0f, C = 0.0f;
  static Set<String> dates = new HashSet<String>();
  static Map<String, StockOHLCData> symbolOnDate = new HashMap<String, StockOHLCData>();

  public static void main(String[] args) throws IOException {
    getOhlcAndEMAData();

    Map<String, StockOHLCData> latestAvailable = new HashMap<String, StockOHLCData>();
    List<String> symbols = Symbols.getSymbols();
    for (String d : dates) {
      StringBuilder csvData = new StringBuilder(header);
      for (String s : symbols) {

        if (symbolOnDate.containsKey(s + ".NS" + "_" + d)) {
          csvData.append(s).append(",").append(d).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getOpen()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getHigh()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getLow()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getClose()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma3d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma5d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma10d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma20d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma30d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma50d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma100d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma150d()).append(",")
              .append(symbolOnDate.get(s + ".NS" + "_" + d).getEma200d()).append("\n");

          latestAvailable.put(s, symbolOnDate.get(s + ".NS" + "_" + d));

        } else {

          if (latestAvailable.containsKey(s)) {
            // case when a stock does not have this day data, use previous available dates data.(IPO
            // or error)
            StockOHLCData symbolOnPreviousDate = latestAvailable.get(s);
            csvData.append(s).append(",").append(d).append(",")
                .append(symbolOnPreviousDate.getOpen()).append(",")
                .append(symbolOnPreviousDate.getHigh()).append(",")
                .append(symbolOnPreviousDate.getLow()).append(",")
                .append(symbolOnPreviousDate.getClose()).append(",")
                .append(symbolOnPreviousDate.getEma3d()).append(",")
                .append(symbolOnPreviousDate.getEma5d()).append(",")
                .append(symbolOnPreviousDate.getEma10d()).append(",")
                .append(symbolOnPreviousDate.getEma20d()).append(",")
                .append(symbolOnPreviousDate.getEma30d()).append(",")
                .append(symbolOnPreviousDate.getEma50d()).append(",")
                .append(symbolOnPreviousDate.getEma100d()).append(",")
                .append(symbolOnPreviousDate.getEma150d()).append(",")
                .append(symbolOnPreviousDate.getEma200d()).append("\n");
          } else {
            csvData.append(s).append(",").append("").append(",").append("").append(",").append("")
                .append(",").append("").append(",").append("").append(",").append("").append(",")
                .append("").append(",").append("").append(",").append("").append(",").append("")
                .append(",").append("").append(",").append("").append(",").append("").append(",")
                .append("\n");
          }
        }
      }
      saveToFile(d, csvData);
    }
  }

  public static void getOhlcAndEMAData() {

    System.out.println("calculating master data....");
    try {

      List<String> symbols = Symbols.getSymbols();

      Calendar from = Calendar.getInstance();
      Calendar to = Calendar.getInstance();
      from.add(Calendar.YEAR, -2); // from 1 year ago


      List<HistoricalQuote> histQuotes = null;
      for (String s : symbols) {
        System.out.println(s + ".NS");
        Stock hstock = YahooFinance.get(s + ".NS");
        
     
        

        try {
          histQuotes = hstock.getHistory(from, to, Interval.DAILY);

        } catch (Exception e) {

          e.printStackTrace();
          System.out.println("yahoo get history exception with - " + s);
          continue;
        }

        for (HistoricalQuote stock : histQuotes) {

          if (j == 0 && stock.getOpen() != null) {
            O = stock.getOpen().floatValue();
            H = stock.getHigh().floatValue();
            L = stock.getLow().floatValue();
            C = stock.getClose().floatValue();
            ema3 = stock.getClose().floatValue();
            ema5 = stock.getClose().floatValue();
            ema10 = stock.getClose().floatValue();
            ema20 = stock.getClose().floatValue();
            ema30 = stock.getClose().floatValue();
            ema50 = stock.getClose().floatValue();
            ema100 = stock.getClose().floatValue();
            ema150 = stock.getClose().floatValue();
            ema200 = stock.getClose().floatValue();

          } else {
            if (stock.getOpen() != null) {

              O = stock.getOpen().floatValue();
              H = stock.getHigh().floatValue();
              L = stock.getLow().floatValue();
              C = stock.getClose().floatValue();

              ema3 = calculateHistoricalEMA(stock.getSymbol(), 3, stock.getClose());
              ema5 = calculateHistoricalEMA(stock.getSymbol(), 5, stock.getClose());
              ema10 = calculateHistoricalEMA(stock.getSymbol(), 10, stock.getClose());
              ema20 = calculateHistoricalEMA(stock.getSymbol(), 20, stock.getClose());
              ema30 = calculateHistoricalEMA(stock.getSymbol(), 30, stock.getClose());
              ema50 = calculateHistoricalEMA(stock.getSymbol(), 50, stock.getClose());
              ema100 = calculateHistoricalEMA(stock.getSymbol(), 100, stock.getClose());
              ema150 = calculateHistoricalEMA(stock.getSymbol(), 150, stock.getClose());
              ema200 = calculateHistoricalEMA(stock.getSymbol(), 200, stock.getClose());
            } else {
              System.out.println("error with " + stock);
            }
          }

          String date = getDateFromCalender(stock.getDate());
          StockOHLCData sd = new StockOHLCData(stock.getSymbol(), date, O, H, L, C, ema3, ema5,
              ema10, ema20, ema30, ema50, ema100, ema150, ema200);

          dates.add(date);
          symbolOnDate.put(stock.getSymbol() + "_" + date, sd);

          j++;
        }

      }
      System.out.println("total - " + j);
    } catch (Exception e) {

      e.printStackTrace();
    }

    return;
  }

  private static String getDateFromCalender(Calendar calendar) {

    int month = calendar.get(Calendar.MONTH) + 1;
    return calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
  }

  public static float calculateHistoricalEMA(String sym, int N, BigDecimal pToday) {
    DecimalFormat df = new DecimalFormat("#.00");

    float EMAyesterday = 0.0f;
    if (N == 3)
      EMAyesterday = ema3;
    if (N == 5)
      EMAyesterday = ema5;
    if (N == 10)
      EMAyesterday = ema10;
    if (N == 20)
      EMAyesterday = ema20;
    if (N == 30)
      EMAyesterday = ema30;
    if (N == 50)
      EMAyesterday = ema50;
    if (N == 100)
      EMAyesterday = ema100;
    if (N == 150)
      EMAyesterday = ema150;
    if (N == 200)
      EMAyesterday = ema200;

    float K = 2.0f / (N + 1.0f);

    float EMAtoday = (pToday.floatValue() * K) + (EMAyesterday * (1 - K));
    EMAtoday = Float.parseFloat(df.format(EMAtoday));
    return EMAtoday;

  }

  public static void saveToFile(String date, StringBuilder csvData) throws IOException {
    if ((new File(path + "volume_price_" + date + ".csv").exists())) {
      new File(path + "volume_price_" + date + ".csv").delete();
    }

    new File(path + "volume_price_" + date + ".csv").createNewFile();

    Files.write(Paths.get(path + "volume_price_" + date + ".csv"), csvData.toString().getBytes(),
        StandardOpenOption.WRITE);
  }

}
