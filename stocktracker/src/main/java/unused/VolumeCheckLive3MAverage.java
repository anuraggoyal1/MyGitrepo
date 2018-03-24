package unused;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.anurag.stocks.Symbols;
import com.anurag.stocks.model.StockData;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class VolumeCheckLive3MAverage {
//	static final Logger logger = Logger.getLogger(VolumeCheckLive3MAverage.class);

	static Map<String, Stock> alreadyFound = new HashMap<String, Stock>();

	static Map<String, StockData> previousDay = null;
	
	static int percent = 0;
	public static void main(String[] args) throws InterruptedException {
		
		if(args.length == 0 ){
			System.out.println("provide percentage for check...");
			return ;
		}
		
		percent = Integer.parseInt(args[0]);

	//	previousDay = loadPreviousDayData();
	//	logger.info("loaded previous day volume data. size is:" + previousDay.size());

		Symbols sym = new Symbols();
		List<String> symbols = sym.getSymbols();
		while (true) {
			while (isMarketOpen()) {
			//	logger.info("starting next round of check.........."+new Date());
				for (String s : symbols) {
					checkCriterian(s + ".NS");
					Thread.currentThread().sleep(100L);
				}
				Thread.currentThread().sleep(1 * 60 * 1000L); // 15 * 60 * 1000L
			}
			Thread.currentThread().sleep(1 * 60 * 1000L);
		}
	}

	private static Map<String, StockData> loadPreviousDayData() {

		String csvFile = "/Users/gpq5/Documents/daily_tracker/previous_day_data.csv";
		previousDay = new HashMap<String, StockData>();
		StockData s = null;
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
					CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
			String[] line;
			while ((line = reader.readNext()) != null) {
				s = new StockData(line[0], Long.parseLong(line[1]), Float.parseFloat(line[2]));
				previousDay.put(line[0], s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return previousDay;
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
			//	logger.info("Market is open now");
				return true;
			} else {
		//		logger.info("Market is closed now");
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void checkCriterian(String symbol) {
		try {
			Stock stock = YahooFinance.get(symbol);
			doVolumePriceCheck(symbol, stock);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean doVolumePriceCheck(String symbol, Stock stock) {
		if (!alreadyFound.containsKey(symbol) && stock.getQuote().getAvgVolume() != null) {
			float threeMonthVolumePercent = (stock.getQuote().getAvgVolume() * percent) / 100;
			float diffVol = ((stock.getQuote().getVolume() - threeMonthVolumePercent)/threeMonthVolumePercent)*100;
			if (stock.getQuote().getVolume() > threeMonthVolumePercent) {
				/*logger.info(symbol +":, VD:"+diffVol +"%, PD: " + stock.getQuote().getChangeInPercent().floatValue() + "%, O:"
						+ stock.getQuote().getOpen() + ", H:" + stock.getQuote().getDayHigh() + ", L:"
						+ stock.getQuote().getDayLow() + ", C:" + stock.getQuote().getPrice());*/

				alreadyFound.put(symbol, stock);
			}
		}

		return false;
	}

}
