package com.anurag.stocks.daily;

import java.io.IOException;
import java.util.List;

import com.anurag.stocks.Symbols;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class DailyEveningAllVolumeCheck {

    //static final Logger logger = Logger.getLogger(DailyEveningAllVolumeCheck.class);

    public static void main(String[] args) throws InterruptedException {
        Symbols sym = new Symbols();
        List<String> symbols = sym.getSymbols();
        Long volume = null;
        Long avgVolume = null;
        float diff = 0;
        float percentDiff = 0;
        System.out.println("this checks today's volume comparing 3M average...");
        for (String s : symbols) {

            try {
                Stock stock = YahooFinance.get(s + ".NS");
                if (stock != null) {
                    volume = stock.getQuote().getVolume();
                    avgVolume = stock.getQuote().getAvgVolume();
                    if (avgVolume != null && volume != null) {
                        diff = volume - avgVolume;
                        percentDiff = (diff / avgVolume) * 100;
                       /* logger.debug(
                                "Symbol="
                                        + stock.getSymbol()
                                        + ", Volume="
                                        + percentDiff
                                        + ", Price="
                                        + stock.getQuote().getChangeInPercent());*/
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread.currentThread().sleep(100L);
        }
    }
}
