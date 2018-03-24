package com.anurag.stocks.daily;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.StockOHLCData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class StocksBelowXDaysEMA {
	static String path = "/Users/anuraggoyal/Documents/daily_tracker/bulk/";
	static Map<String, StockOHLCData> pday, tday;

	public static void main(String[] args) {
		System.out.println("starting check...");
		int c = 0;
		pday = loadPreviousDayData();
		tday = loadTodayData();

		Symbols sym = new Symbols();
		List<String> symbols = sym.getSymbols();
		System.out.println("Total Symbols-"+symbols.size());
		System.out.println("Stocks 5-EMA is below 13-EMA");
		for (String s : symbols) {
			if (tday.get(s).getEma5d() < tday.get(s).getEma13d()) {
				System.out.println(s);
				c++;
			}

		}
		System.out.println("total - " + c);
		
		//---
		System.out.println();
		System.out.println("Stocks Uptrend today...");
		c=0;
		for (String s : symbols) {
			if ((pday.get(s).getEma5d() < pday.get(s).getEma13d()) && (tday.get(s).getEma5d() > tday.get(s).getEma13d())) {
				System.out.println(s);
				c++;
			}

		}
		System.out.println("total - " + c);
		
		//---
		System.out.println();
		System.out.println("Stocks Downtrend today...");
		c=0;
		for (String s : symbols) {
			if ((pday.get(s).getEma5d() > pday.get(s).getEma13d()) && (tday.get(s).getEma5d() < tday.get(s).getEma13d())) {
				System.out.println(s);
				c++;
			}

		}
		System.out.println("total - " + c);		

	}

	private static Map<String, StockOHLCData> loadTodayData() {

		return loadDayData(1);
	}

	private static Map<String, StockOHLCData> loadPreviousDayData() {
		return loadDayData(2);
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

			while ((line = reader.readNext()) != null && line[0] != "") {
				s = new StockOHLCData(line[0], line[1], Float.parseFloat(line[2]),
						Float.parseFloat(line[3]), Float.parseFloat(line[4]), Float.parseFloat(line[5]),
						Float.parseFloat(line[6]), Float.parseFloat(line[7]), Float.parseFloat(line[8]),
						Float.parseFloat(line[9]), Float.parseFloat(line[10]), Float.parseFloat(line[11]),
						Float.parseFloat(line[12]), Float.parseFloat(line[13]), Float.parseFloat(line[14]));
				previousDay.put(line[0], s);
			}
		} catch (IOException e) {
			System.out.println("error on loading day-" + dayNo);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("error on loading day-" + dayNo + " with symbol-" + line[0]);
			e.printStackTrace();
		}
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

}