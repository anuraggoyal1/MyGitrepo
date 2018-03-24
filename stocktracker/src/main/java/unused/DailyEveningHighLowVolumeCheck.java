package unused;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.anurag.stocks.Symbols;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class DailyEveningHighLowVolumeCheck {

//  static final Logger logger = Logger.getLogger(DailyEveningHighLowVolumeCheck.class);

  public static void main(String[] args) throws InterruptedException {

	  System.out.println("this checks today's high low volume comparing 3M average...");
    List<Stock> listOfHighVolume = new LinkedList<Stock>();

    List<Stock> listOfLowVolume = new LinkedList<Stock>();

    Symbols sym = new Symbols();
    List<String> symbols = sym.getSymbols();
    Long volume = null;
    Long avgVolume = null;
    float diff = 0;
    float percentDiff = 0;

    for (String s : symbols) {

      try {
        Stock stock = YahooFinance.get(s + ".NS");
        if (stock != null) {
          volume = stock.getQuote().getVolume();
          avgVolume = stock.getQuote().getAvgVolume();
          if (avgVolume != null && volume != null) {
            diff = volume - avgVolume;
            percentDiff = (diff / avgVolume) * 100;
            if (percentDiff < -25 && stock.getQuote().getChangeInPercent().floatValue() < -0.49)
              listOfLowVolume.add(stock);
            else if (percentDiff > 25 && stock.getQuote().getChangeInPercent().floatValue() > 0.49)
              listOfHighVolume.add(stock);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      Thread.currentThread().sleep(100L);
    }

    //   logger.debug("**** High Volume and High price list ***");
    for (Stock s : listOfHighVolume) {
      volume = s.getQuote().getVolume();
      avgVolume = s.getQuote().getAvgVolume();

      diff = volume - avgVolume;
      percentDiff = (diff / avgVolume) * 100;
    /*  logger.debug(
          "Symbol="
              + s.getSymbol()
              + ", Volume="
              + percentDiff
              + ", Price="
              + s.getQuote().getChangeInPercent());*/
    }

    //  logger.debug("**** Low Volume and Low price list ***");
    for (Stock s : listOfLowVolume) {
      volume = s.getQuote().getVolume();
      avgVolume = s.getQuote().getAvgVolume();

      diff = volume - avgVolume;
      percentDiff = (diff / avgVolume) * 100;
     /* logger.debug(
          "Symbol="
              + s.getSymbol()
              + ", Volume="
              + percentDiff
              + ", Price="
              + s.getQuote().getChangeInPercent());*/
    }
  }
}
