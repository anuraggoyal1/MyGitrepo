package com.anurag.stocks;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.anurag.stocks.model.StockOHLCData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

//This uses previous date file and calculate current EMA doing calculation...
public class GenerateLiveVolumePriceUsingHistoricalEMAFromYahoo {

	static String header = "symbol,open,high,low,close,ema3d,ema5d,ema10d,ema15d,ema30d,ema50d,ema100d,ema150d,ema200d"
			+ "\n";
	static StringBuilder csvData = new StringBuilder(header);

	static String path = "/Users/gpq5/Documents/daily_tracker/";

	static Map<String, StockOHLCData> pday = null;

	public static void main(String[] args) throws IOException {
		System.out.println("generating todays data....");
		
			new File(path + "volume_price_" + getTodayDate() + ".csv").delete();
			
		

		pday = loadPreviousDayData();
		
		Symbols sym = new Symbols();
		List<String> symbols = sym.getSymbols();
		for (String s : symbols) {
			Stock stock = YahooFinance.get(s + ".NS");
			if (stock.getQuote() == null) {
				System.out.println("error with-" + s);
			} else {
				if (stock.getQuote().getVolume() != null && stock.getQuote().getChangeInPercent() != null
						&& stock.getQuote().getAvgVolume() != null) {

					csvData.append(s).append(",")
							.append(getTodayDate()).append(",")
							.append(stock.getQuote().getOpen()).append(",")
							.append(stock.getQuote().getDayHigh()).append(",")
							.append(stock.getQuote().getDayLow()).append(",")
							.append(stock.getQuote().getPrice()).append(",")
							.append(generateEMA(s, 3, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 5, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 10, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 15, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 30, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 50, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 100, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 150, stock.getQuote().getPrice())).append(",")
							.append(generateEMA(s, 200, stock.getQuote().getPrice()))
							
							.append("\n");

				}
			}
			
		}

		saveToFile();
		System.out.println("saved data into file..done");
	}

	public static void saveToFile() throws IOException {
		if (!(new File(path + "volume_price_" + getTodayDate() + ".csv").exists())) {
			new File(path + "volume_price_" + getTodayDate() + ".csv").delete();
			
		}
		new File(path + "volume_price_" + getTodayDate() + ".csv").createNewFile();
		Files.write(Paths.get(path + "volume_price_" + getTodayDate() + ".csv"), csvData.toString().getBytes(),
				StandardOpenOption.WRITE);

	}

	public static String getTodayDate() {
		DateTime dt = new DateTime();
		return Integer.toString(dt.getYear()) + "-" + Integer.toString(dt.getMonthOfYear()) + "-"
				+ Integer.toString(dt.getDayOfMonth());

	}

	private static Map<String, StockOHLCData> loadPreviousDayData() {
		String csvFile = path + "volume_price_" + getDate(1) + ".csv";
		System.out.println("previous day data path is -" + csvFile);

		Map<String, StockOHLCData> previousDay = new HashMap<String, StockOHLCData>();
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
				previousDay.put(line[0], s);
			}
			System.out.println("loaded previous day data...done");
		} catch (IOException e) {
			System.out.println("error on loading previous day-");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("error on loading previous day with symbol-" + line[0]);
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

	public static float generateEMA(String sym, int N, BigDecimal pToday) {
		
		DecimalFormat df = new DecimalFormat("#.00");
		
		float EMAyesterday = 0.0f;
		if (N == 3)
			EMAyesterday = pday.get(sym).getEma3d();
		if (N == 5)
			EMAyesterday = pday.get(sym).getEma5d();
		if (N == 10)
			EMAyesterday = pday.get(sym).getEma10d();
		/*if (N == 15)
			EMAyesterday = pday.get(sym).getEma13d();*/
		if (N == 30)
			EMAyesterday = pday.get(sym).getEma30d();
		if (N == 50)
			EMAyesterday = pday.get(sym).getEma50d();
		if (N == 100)
			EMAyesterday = pday.get(sym).getEma100d();
		if (N == 150)
			EMAyesterday = pday.get(sym).getEma150d();
		if (N == 200)
			EMAyesterday = pday.get(sym).getEma200d();
		
		float K = 2.0f / (N + 1.0f);
		
		float EMAtoday = (pToday.floatValue() * K) + (EMAyesterday * (1 - K));
		EMAtoday = Float.parseFloat(df.format(EMAtoday));

		return EMAtoday;

	}

	public static boolean isMarketOpen() {

		DateTime dt = new DateTime();

		int hour = dt.getHourOfDay();
		int min = dt.getMinuteOfHour();
		int sec = dt.getSecondOfMinute();

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm:ss");

			Date startime = simpleDateFormat.parse("09:15:00");
			Date endtime = simpleDateFormat.parse("15:35:00");

			// current time
			Date current_time = simpleDateFormat.parse(hour + ":" + min + ":" + sec);

			if (current_time.after(startime) && current_time.before(endtime)) {

				return true;
			} else {

				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
}
