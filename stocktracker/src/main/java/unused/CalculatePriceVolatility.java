package unused;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.anurag.stocks.Symbols;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class CalculatePriceVolatility {
	static int i, j = 0;

	static float low = 0.0f, ema5 = 0.0f, ema10 = 0.0f, ema15 = 0.0f, ema30 = 0.0f, ema50 = 0.0f, ema100 = 0.0f,
			ema150 = 0.0f, ema200 = 0.0f;

	public static void main(String[] args) throws IOException {
		getSD();

	}

	public static Map<String, float[]> getSD() {

		System.out.println("calculate volatality lows data....");

		Map<String, float[]> map = new HashMap<String, float[]>();
		try {

			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.MONTH, -3); // from 1 year ago

			Symbols sym = new Symbols();
			List<String> symbols = sym.getSymbols();

			for (String s : symbols) {

				System.out.println(s + ".NS");
				Stock hstock = YahooFinance.get(s + ".NS");
				List<HistoricalQuote> histQuotes = hstock.getHistory(from, to, Interval.DAILY);

				float previousClose = 0.0f;
				
				double[] diffArray = new double[histQuotes.size()];
				for (HistoricalQuote stock : histQuotes) {

					if (stock.getOpen() != null) {

						if (j == 0) {
							previousClose = stock.getClose().floatValue();
							diffArray[j] = 0;
						} else {
							diffArray[j] = ((stock.getClose().floatValue() - previousClose) / previousClose) * 100;
							System.out.println(diffArray[j]);
							previousClose = stock.getClose().floatValue();
						}

					}

					

					j++;
				}
				StandardDeviation sd = new StandardDeviation();
				double sdv = sd.evaluate(diffArray);

				System.out.println(sdv);

				// map.put(s + ".NS", 0.0f);

				i++;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return map;
	}

}
