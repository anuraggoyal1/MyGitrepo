package com.anurag.stocks.daily;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.StockOHLCData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class DailyTrendScreener {
	static String path = "/Users/gpq5/Documents/daily_tracker/";
	static String hpath = "/Users/gpq5/Documents/daily_tracker/";
	static Map<String, StockOHLCData> day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day30;

	static Map<String, String> holdings = new HashMap<String, String>();

	static int maxLotAmount = 5000;

	public static void main(String[] args) {
		System.out.println("starting trend analysis...");
		int count = 0;
		
		String get30CalenderDaysBeforeDate = get30CalenderDaysBeforeDate();
		System.out.println(get30CalenderDaysBeforeDate);

		day30 = loadDayData(get30CalenderDaysBeforeDate());
		
		day10 = loadDayData(getDate(10));
		day9 = loadDayData(getDate(9));
		day8 = loadDayData(getDate(8));
		day7 = loadDayData(getDate(7)); // previous
		day6 = loadDayData(getDate(6)); // latest
		day5 = loadDayData(getDate(5));
		day4 = loadDayData(getDate(4));
		day3 = loadDayData(getDate(3));
		day2 = loadDayData(getDate(2)); // previous
		day1 = loadDayData(getDate(1)); // latest

		System.out.println("\n");
		System.out.println("\n");

		Symbols sym = new Symbols();
		List<String> symbols = sym.getSymbols();
		for (String s : symbols) {
			if (isSwingTrade(s)) {
				
			/*	float day30Price = day30.get(s).getClose();
				float day1Price = day1.get(s).getClose();
				float percentDiff= ((day1Price-day30Price)/day30Price)*100;*/

				System.out.println("Symbol: " + s  +", Qty: " + (int) (maxLotAmount / day1.get(s).getClose()));
				count++;

			}

		}
		System.out.println("Total buy signals - " + count);

		System.out.println("\n");

		// Sell criteria
		Set<String> holdingSym = getHoldings().keySet();
		System.out.println("Sell signals - ");
		for (String s : holdingSym) {
			if (checkSellCriterian(s)) {
				System.out.println(s + ", SL:" + holdings.get(s) + ", CP:" + day1.get(s).getClose());
			}

		}
	}

	private static boolean checkSellCriterian(String sym) {
		
		if (day1.get(sym).getClose() < day1.get(sym).getEma5d())
			return true;

		return false;
	}

	private static boolean isSwingTrade(String sym) {
		int count = 0;

		
		if (day10.containsKey(sym) && day9.containsKey(sym) && day8.containsKey(sym) && day7.containsKey(sym) && day6.containsKey(sym) && day5.containsKey(sym)  && day4.containsKey(sym) && day3.containsKey(sym) && day2.containsKey(sym)
				&& day1.containsKey(sym)  ) {
			/*if (day10.get(sym).getClose() < day10.get(sym).getEma50d())
				count++;
			if (day9.get(sym).getClose() < day9.get(sym).getEma50d())
				count++;
			if (day8.get(sym).getClose() < day8.get(sym).getEma50d())
				count++;
			if (day7.get(sym).getClose() < day7.get(sym).getEma50d())
				count++;
			if (day6.get(sym).getClose() < day6.get(sym).getEma50d())
				count++;*/
			if (day5.get(sym).getClose() < day5.get(sym).getEma50d())
				count++;
			if (day4.get(sym).getClose() < day4.get(sym).getEma50d())
				count++;
			if (day3.get(sym).getClose() < day3.get(sym).getEma50d())
				count++;
			if (day2.get(sym).getClose() < day2.get(sym).getEma50d())
				count++;

			/*if (day1.get(sym).getClose() > day1.get(sym).getEma13d() &&  count == 4)
				return true;*/
		}
		return false;

	}
	
	private static boolean isSwingTradeWith30DaysPriceDiff(String sym) {
		boolean pass = false;

		
		if (day30.containsKey(sym)  && day2.containsKey(sym) && day1.containsKey(sym)  ) {
			float day30Price = day30.get(sym).getClose();
			float day2Price = day2.get(sym).getClose();
			float percentDiff= ((day2Price-day30Price)/day30Price)*100;
			
			if (percentDiff < -5){
				pass=true;
				System.out.println("below 5 match-"+sym);
			}

			if (day1.get(sym).getClose() > day1.get(sym).getEma5d() && pass){
				return true;
			}
		}
		return false;

	}

	private static Map<String, StockOHLCData> loadDayData(String date) {
		String csvFile = path + "volume_price_" + date + ".csv";
		
		Map<String, StockOHLCData> previousDay = new HashMap<String, StockOHLCData>();
		StockOHLCData s = null;
		CSVReader reader = null;
		String[] line = null;
		try {
			reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
					CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

			while ((line = reader.readNext()) != null) {
				
				if (!line[2].trim().equals("")) {
					s = new StockOHLCData(line[0], (line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]),
							Float.parseFloat(line[4]), Float.parseFloat(line[5]), Float.parseFloat(line[6]),
							Float.parseFloat(line[7]), Float.parseFloat(line[8]), Float.parseFloat(line[9]),
							Float.parseFloat(line[10]), Float.parseFloat(line[11]), Float.parseFloat(line[12]),
							Float.parseFloat(line[13]), Float.parseFloat(line[14]));
					previousDay.put(line[0], s);
				} else {
					System.out.println("error loading "+line[0]+" in date"+ date);
				}
			}
			
		} catch (IOException e) {
			System.out.println("error on loading date-" + date);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("error on loading date-" + date + " with symbol-" + line[0]);
			e.printStackTrace();
		}
		return previousDay;
	}

	public static String getDate(int count) {
		int day = count;
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
		System.out.println("day -" + day + " date is -" + date);
		return date;

	}
	
	public static String get30CalenderDaysBeforeDate() {
		DateTime dt = new DateTime();
		dt=dt.minusDays(29);
		String date = "";
		while (true) {
			date = Integer.toString(dt.getYear()) + "-" + Integer.toString(dt.getMonthOfYear()) + "-"
					+ Integer.toString(dt.getDayOfMonth());
			if (new File(path + "volume_price_" + date + ".csv").exists()) {
					break;
			}
			dt = dt.minusDays(1);

		}
		return date;

	}

	/*
	 * private static float checkPercentDifference(String sym) { try {
	 * List<HistoricalQuote> historyData = getHistoryData(sym); float
	 * highestClose = historyData.get(0).getClose().floatValue(); float
	 * lowestClose = historyData.get(0).getClose().floatValue();
	 * 
	 * for (HistoricalQuote q : historyData) { if (q.getClose().floatValue() >
	 * highestClose) highestClose = q.getClose().floatValue(); if
	 * (q.getClose().floatValue() < lowestClose) lowestClose =
	 * q.getClose().floatValue(); }
	 * 
	 * float percentDiff = ((lowestClose - highestClose) / highestClose) * 100;
	 * 
	 * return percentDiff; } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 * 
	 * return 0.0f; } static List<HistoricalQuote> getHistoryData(String sym)
	 * throws IOException { Calendar from = Calendar.getInstance(); Calendar to
	 * = Calendar.getInstance(); from.add(Calendar.DAY_OF_MONTH, -21); // for 10
	 * days
	 * 
	 * Stock hstock = YahooFinance.get(sym); List<HistoricalQuote> histQuotes =
	 * hstock.getHistory(from, to, Interval.DAILY);
	 * 
	 * return histQuotes.subList(histQuotes.size() - 1 - 9, histQuotes.size());
	 * 
	 * }
	 */

	static Map<String, String> getHoldings() {

		String csvFile = hpath + "holdings.csv";

		CSVReader reader = null;
		String[] line = null;
		try {
			reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
					CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

			while ((line = reader.readNext()) != null) {

				holdings.put(line[0], line[1]);
			}

			System.out.println("total holdings - " + holdings.size());

			return holdings;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	}
}
