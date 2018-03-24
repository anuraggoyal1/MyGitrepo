package com.anurag.stocks;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class App {

  static int date;

  

  static Map<String, Stock> vol = new HashMap<String, Stock>();

  static int counterForFiveMinuteInterval = 0;

  static int tradingWindowsCountOfFiveMinutes = 75;
  static int tradingWindowsCountOfFifteenMinutes = 25;
  static int tradingWindowsCountOfThirtyMinutes = 12;

  public static void main(String[] args) throws InterruptedException {

    Symbols sym = new Symbols();
    List<String> symbols = sym.getSymbols();

    while (true) {

      while (isMarketOpen()) {
       // logger.info("starting next round..........");
        counterForFiveMinuteInterval = calculateCurrentCounter();
        if (isTodayNewDay()) {
          vol.clear();
        }

        for (String s : symbols) {
          checkCriterian(s + ".NS");
        }
        Thread.currentThread().sleep(5 * 60 * 1000L);
      }
      Thread.currentThread().sleep(5 * 60 * 1000L);
    }
  }

  private static int calculateCurrentCounter() {
    DateTime dt = new DateTime();
    int hour = dt.getHourOfDay() - 9;
    int min = dt.getMinuteOfHour();
    return hour * 12 + (min / 5) - 3;
  }

  public static boolean isTodayNewDay() {
    Calendar calendar = Calendar.getInstance();

    if (date != calendar.get(Calendar.DAY_OF_MONTH)) {
      date = calendar.get(Calendar.DAY_OF_MONTH);
      return true;
    }
    return false;
  }

  public static boolean isMarketOpen() {

    /*Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
    int min = calendar.get(Calendar.MINUTE);
    int sec = calendar.get(Calendar.SECOND);


    try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm:ss");

        Date startime = simpleDateFormat.parse("09:15:00");
        Date endtime = simpleDateFormat.parse("15:35:00");

        //current time
        Date current_time = simpleDateFormat.parse(hour+":"+min+":"+sec);

        if (current_time.after(startime) && current_time.before(endtime)) {
            logger.info("Market is open now");
            return true;
        } else {
            logger.info("Market is closed now");
            return false;
        }
    } catch (ParseException e) {
        e.printStackTrace();
    }
    return false;*/

    return true;
  }

  public static void checkCriterian(String symbol) {

    try {
      Stock stock = YahooFinance.get(symbol);

      vol.put(getKeyForSymbolCurrent(symbol), stock);

      int v = doVolumeCheck(symbol, stock);
      int p = doPriceCheck(symbol, stock);

      if (v + p > 3) {
    //    logger.info("$$$$$$$$$" + symbol + "$$$$$$$$" + " Strength=" + v + p);
      }

      Thread.currentThread().sleep(500L);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static int doPriceCheck(String symbol, Stock stock) {

    int lCounter = 0;

    float currentChangeInPercent = stock.getQuote().getChangeInPercent().floatValue();
    if (currentChangeInPercent >= 0) lCounter++;

    //5 min changeInPricePercent
    if (vol.get(getKeyForSymbol5MinBefore(symbol)) != null) {
      float changeInPercent5Min =
          vol.get(getKeyForSymbol5MinBefore(symbol)).getQuote().getChangeInPercent().floatValue();
      if (changeInPercent5Min > 0) lCounter++;
    }

    //15 min changeInPricePercent
    if (vol.get(getKeyForSymbol15MinBefore(symbol)) != null) {
      float changeInPercent15Min =
          vol.get(getKeyForSymbol15MinBefore(symbol)).getQuote().getChangeInPercent().floatValue();
      if (changeInPercent15Min > 0) lCounter++;
    }

    //30 min changeInPricePercent
    if (vol.get(getKeyForSymbol30MinBefore(symbol)) != null) {
      float changeInPercent30Min =
          vol.get(getKeyForSymbol30MinBefore(symbol)).getQuote().getChangeInPercent().floatValue();
      if (changeInPercent30Min > 0) {
        lCounter++;
      }
    }

    return lCounter;
  }

  public static int doVolumeCheck(String symbol, Stock stock) {

    int lCounter = 0;
    long volumeAvgFor5Min = 0;
    //5 min vol
    if (stock.getQuote().getAvgVolume() != null) {
      volumeAvgFor5Min = (stock.getQuote().getAvgVolume() / tradingWindowsCountOfFiveMinutes);
    }

    if (vol.get(getKeyForSymbol5MinBefore(symbol)) != null) {
      long liveDifferenceInVolume =
          (stock.getQuote().getVolume()
              - vol.get(getKeyForSymbol5MinBefore(symbol)).getQuote().getVolume());
      if (volumeAvgFor5Min > 0 && liveDifferenceInVolume > volumeAvgFor5Min) {
        float diffInPercent = (liveDifferenceInVolume - volumeAvgFor5Min) / volumeAvgFor5Min;
        if (diffInPercent > 5) {
       //   logger.info(symbol + " has high volume in last 5 mins. Volume jump is: " + diffInPercent + "%");
          lCounter++;
        }
      }
    }

    //15 min vol
    if (stock.getQuote().getAvgVolume() != null
        && vol.get(getKeyForSymbol15MinBefore(symbol)) != null) {
      long volumeAvgFor15Min =
          (stock.getQuote().getAvgVolume() / tradingWindowsCountOfFifteenMinutes);
      long liveDifferenceIn15MinVolume =
          (stock.getQuote().getVolume()
              - vol.get(getKeyForSymbol15MinBefore(symbol)).getQuote().getVolume());

      if (liveDifferenceIn15MinVolume > volumeAvgFor15Min) {
        float diffInPercentFor15 =
            (liveDifferenceIn15MinVolume - volumeAvgFor15Min) / volumeAvgFor15Min;

        if (diffInPercentFor15 > 5) {
         /* logger.info(
              symbol
                  + " has high volume in last 15 mins. Volume jump is: "
                  + diffInPercentFor15
                  + "%");*/
          lCounter++;
        }
      }
    }

    //30 min vol
    if (stock.getQuote().getAvgVolume() != null
        && vol.get(getKeyForSymbol30MinBefore(symbol)) != null) {
      long volumeAvgFor30Min =
          (stock.getQuote().getAvgVolume() / tradingWindowsCountOfThirtyMinutes);
      long liveDifferenceIn30MinVolume =
          (stock.getQuote().getVolume()
              - vol.get(getKeyForSymbol30MinBefore(symbol)).getQuote().getVolume());

      if (liveDifferenceIn30MinVolume > volumeAvgFor30Min) {
        float diffInPercentFor30 =
            (liveDifferenceIn30MinVolume - volumeAvgFor30Min) / volumeAvgFor30Min;

        if (diffInPercentFor30 > 5) {
          /*logger.info(
              symbol
                  + " has high volume in last 30 mins. Volume jump is: "
                  + diffInPercentFor30
                  + "%");*/
          lCounter++;
        }
      }
    }

    return lCounter;
  }

  private static String getKeyForSymbolCurrent(String symbol) {
    return symbol + "-" + counterForFiveMinuteInterval;
  }

  private static String getKeyForSymbol5MinBefore(String symbol) {
    return symbol + "-" + (counterForFiveMinuteInterval - 1);
  }

  private static String getKeyForSymbol15MinBefore(String symbol) {
    return symbol + "-" + (counterForFiveMinuteInterval - 3);
  }

  private static String getKeyForSymbol30MinBefore(String symbol) {
    return symbol + "-" + (counterForFiveMinuteInterval - 6);
  }
}
