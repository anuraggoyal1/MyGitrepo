package com.anurag.stocks;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class BuySellSimulator {
	

	static double initialBalance = 500000;
	static double fundBalance = 0;
	static int balQty = 0;
	static int totalBought = 0;
	static int totalSold = 0;

	public static void main(String[] args) throws IOException {
		getSD();

	}

	public static Map<String, float[]> getSD() {

		System.out.println("simulate buy sell....");

		Map<String, float[]> map = new HashMap<String, float[]>();
		try {

			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.YEAR, -1); // from 1 year ago

			Symbols sym = new Symbols();
			List<String> symbols = sym.getSymbols();

			for (String s : symbols) {
				resetValues();

				//double standardVariation = getStandardVariation(from, to, s);
				// System.out.println(standardVariation);
				simulateBuySell(from, to, s, 5, 0, 4, 0);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return map;
	}

	public static void resetValues() {
		fundBalance = initialBalance;
		balQty = 0;
		totalBought = 0;
		totalSold = 0;
	}

	private static void simulateBuySell(Calendar from, Calendar to, String s, int buyQty, int sellQty,
			double buyTrigger, double sellTrigger) throws IOException {
		System.out.println(s + ".NS");
		Stock hstock = YahooFinance.get(s + ".NS");
		List<HistoricalQuote> histQuotes = hstock.getHistory(from, to, Interval.DAILY);

		int j = 0;
		float previousClose = 0.0f;
		double diff = 0.0;
		// double[] diffArray = new double[histQuotes.size()];
		for (HistoricalQuote stock : histQuotes) {

			if (stock.getOpen() != null) {

				if (j == 0) {
					diff = 0;
				} else {
					diff = ((stock.getClose().floatValue() - previousClose) / previousClose) * 100;

					if (diff < -buyTrigger)
						buy(stock, buyQty);
					// if (diff < sellTrigger)
					// sell(stock, sellQty);

				}

				previousClose = stock.getClose().floatValue();

			}
			j++;
		}

		double avgPrice = (initialBalance - fundBalance) / balQty;
		double pl = ((previousClose - avgPrice) / avgPrice) * 100;

		System.out.println("symbol-" + s +", buyTrigger-" + buyTrigger + ", qtyBought-" + totalBought + ", qtySold-" + totalSold + ", Fund_Balance-"
				+ fundBalance + ", current_qty-" + balQty + ", avg_price - " + avgPrice + ", todayClosePrice-"
				+ previousClose + ", %P/L: " + pl);

		return;

	}

	private static void sell(HistoricalQuote stock, int qty) {

		double sCost = stock.getClose().floatValue() * qty;
		if (balQty < qty) {
			System.out.println("not enough qty to sell ");
			return;
		}
		fundBalance = fundBalance + sCost;
		balQty = balQty - qty;
		totalSold = totalSold + qty;
	}

	private static void buy(HistoricalQuote stock, int qty) {
		double pCost = stock.getClose().floatValue() * qty;
		if (fundBalance < pCost) {
			System.out.println("low balance");
			return;
		}
		fundBalance = fundBalance - pCost;
		balQty = balQty + qty;
		totalBought = totalBought + qty;

	}

	public static double getStandardVariation(Calendar from, Calendar to, String s) throws IOException {
		System.out.println(s + ".NS");
		Stock hstock = YahooFinance.get(s + ".NS");
		List<HistoricalQuote> histQuotes = hstock.getHistory(from, to, Interval.DAILY);

		int j = 0;
		float previousClose = 0.0f;

		double[] diffArray = new double[histQuotes.size()];
		for (HistoricalQuote stock : histQuotes) {

			if (stock.getOpen() != null) {

				if (j == 0) {
					diffArray[j] = 0;
				} else {
					diffArray[j] = ((stock.getClose().floatValue() - previousClose) / previousClose) * 100;
				}
				previousClose = stock.getClose().floatValue();
			}
			j++;
		}
		StandardDeviation sd = new StandardDeviation();
		double sdv = sd.evaluate(diffArray);

		return sdv;

	}

}
