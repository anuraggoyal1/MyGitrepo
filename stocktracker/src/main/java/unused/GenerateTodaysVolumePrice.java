package unused;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.StockFullData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class GenerateTodaysVolumePrice {

	static String header = "symbol,3MAvgVol,volume,volumeDiff,priceDiff,open,high,low,close,prev-close,ema3d,ema5d,ema10d,ema15d"
			+ "\n";
	static StringBuilder csvData = new StringBuilder(header);

	static String path = "/Users/gpq5/Documents/daily_tracker/";

	static Map<String, StockFullData> pday = null;

	public static void main(String[] args) throws IOException {
		System.out.println("generating todays data....");

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

					long vol = stock.getQuote().getVolume();
					long avgVol = stock.getQuote().getAvgVolume();

					float diff = vol - avgVol;
					float volDiff = (diff / avgVol) * 100f;

					csvData.append(stock.getSymbol()).append(",").append(stock.getQuote().getAvgVolume()).append(",")
							.append(stock.getQuote().getVolume()).append(",").append(Float.toString(volDiff))
							.append(",").append(stock.getQuote().getChangeInPercent()).append(",")
							.append(stock.getQuote().getOpen()).append(",").append(stock.getQuote().getDayHigh())
							.append(",").append(stock.getQuote().getDayLow()).append(",")
							.append(stock.getQuote().getPrice()).append(",").append(stock.getQuote().getPreviousClose())
							.append(",").append(generateEMA(stock.getSymbol(), 3, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 5, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 10, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 15, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 30, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 50, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 100, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 150, stock.getQuote().getPrice()))
							.append(",").append(generateEMA(stock.getSymbol(), 200, stock.getQuote().getPrice()))
							.append("\n");

				}
			}
		}

		saveToFile();
		System.out.println("done with generating todays data....");
	}

	public static void saveToFile() throws IOException {
		if (!(new File(path + "volume_price_" + getTodayDate() + ".csv").exists())) {
			new File(path + "volume_price_" + getTodayDate() + ".csv").createNewFile();
		}

		Files.write(Paths.get(path + "volume_price_" + getTodayDate() + ".csv"), csvData.toString().getBytes(),
				StandardOpenOption.WRITE);

	}

	public static String getTodayDate() {
		DateTime dt = new DateTime();
		return Integer.toString(dt.getYear()) + "-" + Integer.toString(dt.getMonthOfYear()) + "-"
				+ Integer.toString(dt.getDayOfMonth());

	}

	private static Map<String, StockFullData> loadPreviousDayData() {
		String csvFile = path + "volume_price_" + getDate(1) + ".csv";
		System.out.println("previous day data path is -" + csvFile);

		Map<String, StockFullData> previousDay = new HashMap<String, StockFullData>();
		StockFullData s = null;
		CSVReader reader = null;
		String[] line = null;
		try {
			reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
					CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

			while ((line = reader.readNext()) != null) {
				s = new StockFullData(line[0], Long.parseLong(line[1]), Long.parseLong(line[2]),
						Float.parseFloat(line[3]), Float.parseFloat(line[4]), Float.parseFloat(line[5]),
						Float.parseFloat(line[6]), Float.parseFloat(line[7]), Float.parseFloat(line[8]),
						Float.parseFloat(line[9]), Float.parseFloat(line[10]), Float.parseFloat(line[11]),
						Float.parseFloat(line[12]), Float.parseFloat(line[13]), Float.parseFloat(line[14]),
						Float.parseFloat(line[15]), Float.parseFloat(line[16]), Float.parseFloat(line[17]),
						Float.parseFloat(line[18]));
				previousDay.put(line[0], s);
			}
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
		dt = dt.minusDays(1); // start with yesterday
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
		float K = 2.0f / (N + 1.0f);
		float EMAyesterday = 0.0f;
		if (N == 3)
			EMAyesterday = pday.get(sym).getEma3d();
		if (N == 5)
			EMAyesterday = pday.get(sym).getEma5d();
		if (N == 10)
			EMAyesterday = pday.get(sym).getEma10d();
		if (N == 15)
			EMAyesterday = pday.get(sym).getEma15d();

		float EMAtoday = (pToday.floatValue() * K) + (EMAyesterday * (1 - K));

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
