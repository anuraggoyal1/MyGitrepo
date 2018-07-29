package com.anurag.stocks;

import java.io.IOException;
import java.util.List;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

public class CheckHighVolumeStocksToday {


  public static void main(String[] args) throws IOException {
    getOhlcAndEMAData();
  }

  public static void getOhlcAndEMAData() {

    System.out.println("calculating master data....");
    int i = 0;
    int j = 0;
    try {

      Symbols sym = new Symbols();
      List<String> symbols = sym.getSymbols();

      for (String s : symbols) {
        // System.out.println(s + ".NS");
        Stock hstock = YahooFinance.get(s + ".NS");

        // check today's vol
        StockQuote quote = hstock.getQuote();

        if (quote == null || quote.getChangeInPercent() == null || quote.getVolume() == null
            || quote.getAvgVolume() == null) {
          
          System.out.println("error with - "+s);
          j++;


        } else if (quote.getVolume() > 2 * quote.getAvgVolume()
            && quote.getChangeInPercent().floatValue() > 1) {
      
          System.out.println(s + " 200Avg Price:"+quote.getPriceAvg200() +" change % +" + quote.getChangeInPercent().floatValue());
          i++;
        }

      }
      // end check vol


      System.out.println("total error - " + j);
      System.out.println("total matched - " + i);
    } catch (Exception e) {

      e.printStackTrace();
    }

    return;
  }



}
