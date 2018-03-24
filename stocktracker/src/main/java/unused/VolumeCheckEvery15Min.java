package unused;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.anurag.stocks.Symbols;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class VolumeCheckEvery15Min {

	//static final Logger logger = Logger.getLogger(VolumeCheckEvery15Min.class);

	static Map<String, Stock> oldQuote = new HashMap<String, Stock>();

	static int tradingWindowsCountOfFifteenMinutes = 25;

	public static void main(String[] args) throws InterruptedException {

		Symbols sym = new Symbols();
		List<String> symbols = sym.getSymbols();

		while (true) {

			while (isMarketOpen()) {
				//logger.info("starting next round of check..........");
				for (String s : symbols) {
					checkCriterian(s + ".NS");
					Thread.currentThread().sleep(100L);
				}
				Thread.currentThread().sleep(15 * 60 * 1000L); //15 * 60 * 1000L
			}
			Thread.currentThread().sleep(1 * 60 * 1000L);
		}
	}

	// 9.16 means - 0 and (9.31 means 1)
	private static int calculateCurrentPeriodCounter() {
		DateTime dt = new DateTime();
		int hour = dt.getHourOfDay() - 9;
		int min = dt.getMinuteOfHour() - 15;
		return hour * 4 + (min / 15);
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
				//logger.info("Market is open now");
				return true;
			} else {
				//logger.info("Market is closed now");
				return false;
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
			oldQuote.put(symbol, stock);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void doVolumePriceCheck(String symbol, Stock stock) {

		float diff_3M_Avg_InPercentFor15M = getLastFifteenMinVolumeDifferenceFrom3MAverageInPercent(symbol, stock);

		if (diff_3M_Avg_InPercentFor15M > 10 && stock.getQuote().getChangeInPercent().floatValue() > 1) {

			float liveDiffFromTodaysAvgVolInPercent = getLastFifteenMinVolumeDifferenceFromTodaysAverageInPercent(
					symbol, stock);

			float liveDifferenceFrom10DAvgVolInPercent = getLastFifteenMinVolumeDifferenceFrom10DAverageInPercent(
					symbol, stock);

		/*	logger.info(symbol + " : 3M vol :" + diff_3M_Avg_InPercentFor15M + "%, 10D vol :"
					+ liveDifferenceFrom10DAvgVolInPercent + "% today's vol avg :" + liveDiffFromTodaysAvgVolInPercent
					+ "%, price : " + stock.getQuote().getChangeInPercent().floatValue() + "%, O :"
					+ stock.getQuote().getOpen() + ", L :" + stock.getQuote().getDayLow() + ", H :"
					+ stock.getQuote().getDayHigh());*/
		}

		return;
	}

	public static float getLastFifteenMinVolumeDifferenceFrom3MAverageInPercent(String symbol, Stock stock) {

		if (stock.getQuote().getAvgVolume() != null && oldQuote.get(symbol) != null) {
			long volume3MAvgFor15Min = stock.getQuote().getAvgVolume() / tradingWindowsCountOfFifteenMinutes;
			long liveDifferenceIn15MinVolume = (stock.getQuote().getVolume()
					- oldQuote.get(symbol).getQuote().getVolume());
			float diffInPercent = ((liveDifferenceIn15MinVolume - volume3MAvgFor15Min) / volume3MAvgFor15Min) * 100;
			return diffInPercent;

		}

		return 0.0f;
	}

	public static float getLastFifteenMinVolumeDifferenceFrom10DAverageInPercent(String symbol, Stock stock) {
		if (stock.getQuote().getAvgVolume10D() != null) {
			long volumeAvgFor10DFifteenMinSoFar = stock.getQuote().getAvgVolume10D()
					/ tradingWindowsCountOfFifteenMinutes;
			float liveDifferenceFrom10DAvgVolInPercent = ((stock.getQuote().getVolume()
					- oldQuote.get(symbol).getQuote().getVolume()) / volumeAvgFor10DFifteenMinSoFar) * 100;
			return liveDifferenceFrom10DAvgVolInPercent;
		}

		return 0.0f;
	}

	public static float getLastFifteenMinVolumeDifferenceFromTodaysAverageInPercent(String symbol, Stock stock) {
		long fifteenMinWindowCounter = calculateCurrentPeriodCounter();
		if (fifteenMinWindowCounter != 0) {
			long volumeAvgForTodayFifteenMinSoFar = oldQuote.get(symbol).getQuote().getVolume()
					/ fifteenMinWindowCounter;
			float diffFromTodaysAvgVolInPercent = ((stock.getQuote().getVolume() - volumeAvgForTodayFifteenMinSoFar)
					/ volumeAvgForTodayFifteenMinSoFar) * 100;
			return diffFromTodaysAvgVolInPercent;
		}

		return 0.0f;
	}

}
