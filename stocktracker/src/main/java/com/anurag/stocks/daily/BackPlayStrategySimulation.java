package com.anurag.stocks.daily;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.StockOHLCData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class BackPlayStrategySimulation {
	static String path = "/Users/gpq5/Documents/daily_tracker/bulk/";

	static ArrayList<Map<String, StockOHLCData>> data = new ArrayList<Map<String, StockOHLCData>>();

	static int maxLotAmount = 5000;

	static double initialBalance = 5000;
	static double fundBalance = 0;
	static int balQty = 0;
	static int totalBought = 0;
	static int totalSold = 0;

	public static void main(String[] args) {
		System.out.println("starting trend analysis...");

		// latest data is on index 0
		for (int i = 1; i <= 742; i++) {
			fundBalance = initialBalance;
			balQty = 0;
			data.add(loadDayDataFromBulk(i));

		}

		System.out.println("loaded all bulk data\n");
		System.out.println("\n");

		Symbols sym = new Symbols();
		List<String> symbols = sym.getSymbols();
		for (String s : symbols) {
			trackStock(s);

		}

		System.out.println("\n");

	}

	private static void trackStock(String sym) {
		System.out.println(sym);
		float sl = 0;
		float maxHigh = 918;
		int up = 0, down = 0;
		fundBalance = initialBalance;
		int loop = 0;
		for (int i = data.size() - 1; i >= 15; i--) {
			Map<String, StockOHLCData> day1 = data.get(i); // day1 is oldest
															// like 25
			Map<String, StockOHLCData> day2 = data.get(i - 1); // 26
			Map<String, StockOHLCData> day3 = data.get(i - 2);// 27
			Map<String, StockOHLCData> day4 = data.get(i - 3);
			Map<String, StockOHLCData> day5 = data.get(i - 4);
			/*
			 * Map<String, StockOHLCData> day6 = data.get(i - 5); Map<String,
			 * StockOHLCData> day7 = data.get(i - 6); Map<String, StockOHLCData>
			 * day8 = data.get(i - 7); Map<String, StockOHLCData> day9 =
			 * data.get(i - 8); Map<String, StockOHLCData> day10 = data.get(i -
			 * 9); Map<String, StockOHLCData> day11 = data.get(i - 10);
			 * Map<String, StockOHLCData> day12 = data.get(i - 11); Map<String,
			 * StockOHLCData> day13 = data.get(i - 12); Map<String,
			 * StockOHLCData> day14 = data.get(i - 13); Map<String,
			 * StockOHLCData> day15 = data.get(i - 14);
			 */

			int count = 0;
			if (day1.get(sym) != null && day2.get(sym) != null && day3.get(sym) != null && day4.get(sym) != null
					&& day5.get(sym) != null && day1.get(sym).getDate() != null && day2.get(sym).getDate() != null
					&& day3.get(sym).getDate() != null && day4.get(sym).getDate() != null
					&& day5.get(sym).getDate() != null) {

				/*
				 * if (day1.get(sym).getClose() < day1.get(sym).getEma5d()) {
				 * count++; } if (day2.get(sym).getClose() <
				 * day2.get(sym).getEma5d()) { count++; } if
				 * (day3.get(sym).getClose() < day3.get(sym).getEma5d()) {
				 * count++; } if (day4.get(sym).getClose() <
				 * day4.get(sym).getEma5d()) { count++; } if
				 * (day5.get(sym).getClose() < day5.get(sym).getEma5d()) {
				 * count++; } if (day6.get(sym).getClose() <
				 * day6.get(sym).getEma5d()) { count++; } if
				 * (day7.get(sym).getClose() < day7.get(sym).getEma5d()) {
				 * count++; } if (day8.get(sym).getClose() <
				 * day8.get(sym).getEma5d()) { count++; } if
				 * (day9.get(sym).getClose() < day9.get(sym).getEma5d()) {
				 * count++; } if (day10.get(sym).getClose() <
				 * day10.get(sym).getEma5d()) { count++; } if
				 * (day11.get(sym).getClose() < day11.get(sym).getEma5d()) {
				 * count++; } if (day12.get(sym).getClose() <
				 * day12.get(sym).getEma5d()) { count++; } if
				 * (day13.get(sym).getClose() < day13.get(sym).getEma5d()) {
				 * count++; }
				 */

				int c1 = 0;

				if (day4.get(sym).getClose() < day4.get(sym).getEma5d())
					c1++;
				if (day3.get(sym).getClose() < day3.get(sym).getEma5d())
					c1++;
				if (day2.get(sym).getClose() < day2.get(sym).getEma5d())
					c1++;
				
				if (day1.get(sym).getClose() < day1.get(sym).getEma5d())
					c1++;

				if (c1 >= 3 && day5.get(sym).getClose() > day5.get(sym).getEma5d()) {
					sl = day3.get(sym).getClose();
					int qty = (int) (maxLotAmount / day5.get(sym).getClose());
					System.out.println("Buy call - Symbol: " + sym + ", Date: " + day5.get(sym).getDate() + ", Qty: "
							+ qty + " price -" + day5.get(sym).getClose());
					float buycost = day5.get(sym).getClose() * qty;
					fundBalance = fundBalance - buycost;
					balQty = balQty + qty;
					// System.out.println("up:"+day4.get(sym).getDate());
					if (day5.get(sym).getClose() > day5.get(sym).getEma5d()) {

						up++;
					} else {
						// System.out.println("down:"+day5.get(sym).getDate());
						down++;
					}

				}

				/*
				 * else { if (balQty > 0 && day1.get(sym).getClose() <
				 * day1.get(sym).getOpen() && day2.get(sym).getClose() <
				 * day2.get(sym).getOpen() ) {
				 * 
				 * System.out.println("sell call - Symbol: " + sym + ", Date: "
				 * + day15.get(sym).getDate() + ", Qty: " + balQty + " price -"
				 * + day15.get(sym).getClose()); System.out.println(); float
				 * sellcost = day15.get(sym).getClose() * balQty; fundBalance =
				 * fundBalance + sellcost; balQty = 0;
				 * 
				 * }
				 * 
				 * }
				 */
				loop++;
			}
		}

		System.out.println("total up:" + up + " down:" + down);
		// System.out.println("sym: "+sym+"iniial bal:"+initialBalance +"
		// fundBalNow:"+fundBalance +" final P/L: " +
		// ((fundBalance-initialBalance)/initialBalance)*100+ ", loop: " +
		// loop);

	}

	private static Map<String, StockOHLCData> loadDayDataFromBulk(int dayNo) {
		String csvFile = path + "volume_price_" + getDate(dayNo) + ".csv";
		System.out.println("day -" + dayNo + " path is -" + csvFile);

		Map<String, StockOHLCData> dayData = new HashMap<String, StockOHLCData>();

		StockOHLCData s = null;
		CSVReader reader = null;
		String[] line = null;
		try {
			reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
					CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

			while ((line = reader.readNext()) != null) {
				s = new StockOHLCData(line[0], (line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]),
						Float.parseFloat(line[4]), Float.parseFloat(line[5]), Float.parseFloat(line[6]),
						Float.parseFloat(line[7]), Float.parseFloat(line[8]), Float.parseFloat(line[9]),
						Float.parseFloat(line[10]), Float.parseFloat(line[11]), Float.parseFloat(line[12]),
						Float.parseFloat(line[13]), Float.parseFloat(line[14]));

				dayData.put(line[0], s);

			}

		} catch (IOException e) {
			System.out.println("error on loading day-" + dayNo);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("error on loading day-" + dayNo + " with symbol-" + line[0]);
			e.printStackTrace();
		}
		return dayData;
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
