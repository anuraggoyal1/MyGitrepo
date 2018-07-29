package com.anurag.stocks.daily;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.StockOHLCData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class StocksDailyBelowXDaysEMA {
  static String path = "/Users/anuraggoyal/Documents/daily_tracker/bulk/daily/";
  static String path_near_200 = "/Users/anuraggoyal/Documents/daily_tracker/bulk/near_200/";
  static Map<String, StockOHLCData>  tday, pday, thirdDay, fourthDay, fifthDay, sixthDay, seventhDay, eighthDay, ninthDay, tenthDay;
  static List<String> breakDown20EMA, breakDown50EMA, breakDown100EMA, breakDown200EMA;

  static List<String> breakOut20EMA, breakOut50EMA, breakOut100EMA, breakOut200EMA;

  public static void main(String[] args) throws IOException {
    System.out.println("starting check...");
    breakDown20EMA = new LinkedList<String>();
    breakDown50EMA = new LinkedList<String>();
    breakDown100EMA = new LinkedList<String>();
    breakDown200EMA = new LinkedList<String>();
    breakOut20EMA = new LinkedList<String>();
    breakOut50EMA = new LinkedList<String>();
    breakOut100EMA = new LinkedList<String>();
    breakOut200EMA = new LinkedList<String>();

    int c = 0;
    tenthDay = loadDayData(10);
    ninthDay = loadDayData(9);
    eighthDay = loadDayData(8);
    seventhDay = loadDayData(7);
    sixthDay = loadDayData(6);
    fifthDay = loadDayData(5);
    fourthDay = loadDayData(4);
    thirdDay = loadDayData(3);
    pday = loadDayData(2);
    tday = loadDayData(1);

    Symbols sym = new Symbols();
    List<String> symbols = sym.getSymbols();
    System.out.println("Total Symbols-" + symbols.size());
  

    /*
     * System.out.println("Stocks 5-EMA is below 13-EMA"); for (String s : symbols) { if
     * (tday.get(s).getEma5d() < tday.get(s).getEma20d()) { System.out.println(s); c++; }
     * 
     * } System.out.println("Total SYMBOLS- " + c);
     */

 //------------------------------------------------------------------------------------------------------------------       
    // ---
/*    System.out.println("\n");
    System.out
        .println("........................All Stocks 100EMA Breakout........................");
    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        if ((pday.get(s).getClose() < pday.get(s).getEma100d())
            && (tday.get(s).getClose() > tday.get(s).getEma100d())) {
          System.out.println(s +" "+tday.get(s).getClose() +" "+ tday.get(s).getEma100d() );
        }
      }
    }
    System.out.println("\n");*/

    // ---
    System.out
    .println("........................All Stocks 200EMA Breakout........................");
    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        /*if ((tenthDay.get(s).getClose() < tenthDay.get(s).getEma200d() &&  ninthDay.get(s).getClose() < ninthDay.get(s).getEma200d() && eighthDay.get(s).getClose() < eighthDay.get(s).getEma200d() && seventhDay.get(s).getClose() < seventhDay.get(s).getEma200d() && sixthDay.get(s).getClose() < sixthDay.get(s).getEma200d() && fifthDay.get(s).getClose() < fifthDay.get(s).getEma200d() &&
            fourthDay.get(s).getClose() < fourthDay.get(s).getEma200d() && thirdDay.get(s).getClose() < thirdDay.get(s).getEma200d() && 
            pday.get(s).getClose() < pday.get(s).getEma200d())
            && (tday.get(s).getClose() > tday.get(s).getEma200d())) {*/
        if (( 
            pday.get(s).getClose() < pday.get(s).getEma200d())
            && (tday.get(s).getClose() > tday.get(s).getEma200d())) {
          System.out.println(s +" "+tday.get(s).getClose() +" "+ tday.get(s).getEma200d() );
        }
      }
    }
    System.out.println("\n");
    // ---
    
    // ---stocks near 200 EMA
    int stocksNear200DaysEMA = 0;
    StringBuilder near_200 = new StringBuilder();
    System.out
    .println("........................All Stocks near 200EMA........................");
    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        float diff = tday.get(s).getClose() - tday.get(s).getEma200d();
        float diffInPer = (diff *100) / tday.get(s).getEma200d() ;
        
        if (diffInPer > 0 && diffInPer < 5 ) {
          System.out.println(s +" diff "+ diffInPer+" close price "+tday.get(s).getClose() +" and 200EMA "+tday.get(s).getEma200d());
          near_200.append(s).append(".NS").append("\n");
          stocksNear200DaysEMA++;
        }
        saveToFile(getDateFromCalender(), near_200);
      }
    }
    System.out.println("Total stocks near 200 EMA- " + stocksNear200DaysEMA);
    System.out.println("\n");
    System.out.println("\n");  
    
//------------------------------------------------------------------------------------------------------------------    
/*    int stocksBelow100DaysEMA = 0;
    int stocksBelow200DaysEMA = 0;
   
    int stocksBelow50DaysEMA = 0;
    int stocksBelow30DaysEMA = 0;
    
    // ---count below 30 EMA
    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        if (tday.get(s).getClose() < tday.get(s).getEma30d())
          stocksBelow30DaysEMA++;
      }
    }
    System.out.println("Total stocks below 30 EMA- " + stocksBelow30DaysEMA);*/
    // ---END
    
    // ---count below 50 EMA
/*    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        if (tday.get(s).getClose() < tday.get(s).getEma50d())
          stocksBelow50DaysEMA++;
      }
    }
    System.out.println("Total stocks below 50 EMA- " + stocksBelow50DaysEMA);  */
    // ---END

    // ---count below 100 EMA
/*    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        if (tday.get(s).getClose() < tday.get(s).getEma100d())
          stocksBelow100DaysEMA++;
      }
    }
    System.out.println("Total stocks below 100 EMA- " + stocksBelow100DaysEMA); */
    // ---END



    // ---count below 200 EMA
/*    for (String s : symbols) {
      if (pday.containsKey(s) && tday.containsKey(s)) {
        if (tday.get(s).getClose() < tday.get(s).getEma200d())
          stocksBelow200DaysEMA++;
      }
    }
    System.out.println("Total stocks below 200 EMA- " + stocksBelow200DaysEMA);*/
    // --- END
    
    
   

  //------------------------------------------------------------------------------------------------------------------    
    /*
     * System.out.println("starting of breakout  5EMA > 20EMA today..."); for (String s : symbols) {
     * if ((pday.get(s).getEma5d() < pday.get(s).getEma20d()) && (tday.get(s).getEma5d() >
     * tday.get(s).getEma20d())) { System.out.println(s); } }
     * 
     * System.out.println(); System.out.println();
     * 
     * System.out.println("Middle of breakout  10EMA > 20EMA today..."); for (String s : symbols) {
     * if ((pday.get(s).getEma10d() < pday.get(s).getEma20d()) && (tday.get(s).getEma10d() >
     * tday.get(s).getEma20d())) { System.out.println(s); } }
     * 
     * System.out.println(); System.out.println();
     * 
     * System.out.println("Final breakout 20EMA > 50EMA today..."); for (String s : symbols) { if
     * ((pday.get(s).getEma20d() < pday.get(s).getEma50d()) && (tday.get(s).getEma20d() >
     * tday.get(s).getEma50d())) { System.out.println(s); } }
     * 
     * 
     * System.out.println();
     * System.out.println("........................Holdings  Status........................");
     * 
     * System.out.println("starting of breakdown  20EMA < 30EMA today..."); for (String s : symbols)
     * { if ((pday.get(s).getEma20d() > pday.get(s).getEma30d()) && (tday.get(s).getEma20d() <
     * tday.get(s).getEma30d())) { System.out.println(s); } }
     * 
     * System.out.println(); System.out.println();
     * 
     * System.out.println("Holdings Breakdown 20EMA < 50EMA..."); for (String s : hSymbols) { if
     * (tday.get(s).getEma20d() < tday.get(s).getEma50d()) { System.out.println(s); } }
     * 
     * 
     * 
     */

  }

  private static Map<String, StockOHLCData> loadTodayData() {

    return loadDayData(1);
  }

 
  private static Map<String, StockOHLCData> loadDayData(int dayNo) {
    String csvFile = path + "volume_price_" + getDate(dayNo) + ".csv";
    System.out.println("day -" + dayNo + " path is -" + csvFile);

    Map<String, StockOHLCData> previousDay = new HashMap<String, StockOHLCData>();
    StockOHLCData s = null;
    CSVReader reader = null;
    String[] line = null;
    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

      while ((line = reader.readNext()) != null) {
        if (!line[2].isEmpty()) {
          
          s = new StockOHLCData(line[0], line[1], Float.parseFloat(line[2]),
              Float.parseFloat(line[3]), Float.parseFloat(line[4]), Float.parseFloat(line[5]),
              Float.parseFloat(line[6]), Float.parseFloat(line[7]), Float.parseFloat(line[8]),
              Float.parseFloat(line[9]), Float.parseFloat(line[10]), Float.parseFloat(line[11]),
              Float.parseFloat(line[12]), Float.parseFloat(line[13]), Float.parseFloat(line[14]));
          previousDay.put(line[0], s);
        } else {
          System.out.println("Not able to load symbol " + line[0] + " for day " + dayNo);
        }
      }
    } catch (IOException e) {
      System.out.println("error on loading day-" + dayNo);
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("error on loading day-" + dayNo + " with symbol-" + line[0]);
      e.printStackTrace();
    }

    System.out.println("loaded total stocks from day " + dayNo + "-" + previousDay.size());
    return previousDay;
  }

  public static String getDate(int count) {
    DateTime dt = new DateTime();
    String date = "";
    while (true) {
      date = Integer.toString(dt.getYear()) + "-" + Integer.toString(dt.getMonthOfYear()) + "-"
          + Integer.toString(dt.getDayOfMonth());
      if (new File(path + "volume_price_" + date + ".csv").exists()) {
        count--;
        if (count == 0)
          break;
      }
      dt = dt.minusDays(1);

    }
    return date;

  }
  public static void saveToFile(String date, StringBuilder csvData) throws IOException {
    if ((new File(path_near_200 + "near_200_" + date + ".csv").exists())) {
      new File(path_near_200 + "near_200_" + date + ".csv").delete();
    }

    new File(path_near_200 + "near_200_" + date + ".csv").createNewFile();

    Files.write(Paths.get(path_near_200 + "near_200_" + date + ".csv"), csvData.toString().getBytes(),
        StandardOpenOption.WRITE);

  }
  
  private static String getDateFromCalender() {
    Calendar calendar = Calendar.getInstance();
    int month = calendar.get(Calendar.MONTH) + 1;
    return calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
  }

}
