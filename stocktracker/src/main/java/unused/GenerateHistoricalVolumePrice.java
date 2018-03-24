package unused;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anurag.stocks.Symbols;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class GenerateHistoricalVolumePrice {
	static int i, j = 0;

	static float ema3 = 0.0f, ema5 = 0.0f, ema10 = 0.0f, ema15 = 0.0f, ema30 = 0.0f, ema50 = 0.0f, ema100 = 0.0f, ema150 = 0.0f, ema200 = 0.0f;

	public static void main(String[] args) throws IOException {
	//	getEMA();
		
	}

	public static Map<String, float[]> getEMA() {
		
		System.out.println("calculating ema data....");
		
		
		Map<String, float[]> map = new HashMap<String, float[]>();
		try {
			
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.YEAR, -2); // from 1 year ago

			Symbols sym = new Symbols();
			List<String> symbols = sym.getSymbols();

			for (String s : symbols) {

				System.out.println(s + ".NS");
				Stock hstock = YahooFinance.get(s + ".NS");
				List<HistoricalQuote> histQuotes = hstock.getHistory(from, to, Interval.DAILY);

				float[] f = new float[9];

				for (HistoricalQuote stock : histQuotes) {

					if (stock.getOpen() != null) {
						
						

						if (j == 0) {
							ema3 = stock.getClose().floatValue();
							ema5 = stock.getClose().floatValue();
							ema10 = stock.getClose().floatValue();
							ema15 = stock.getClose().floatValue();
							ema30 = stock.getClose().floatValue();
							ema50 = stock.getClose().floatValue();
							ema100 = stock.getClose().floatValue();
							ema150 = stock.getClose().floatValue();
							ema200 = stock.getClose().floatValue();
						} else {
							ema3 = calculateHistoricalEMA(stock.getSymbol(), 3, stock.getClose());
							ema5 = calculateHistoricalEMA(stock.getSymbol(), 5, stock.getClose());
							ema10 = calculateHistoricalEMA(stock.getSymbol(), 10, stock.getClose());
							ema15 = calculateHistoricalEMA(stock.getSymbol(), 15, stock.getClose());
							ema30 = calculateHistoricalEMA(stock.getSymbol(), 30, stock.getClose());
							ema50 = calculateHistoricalEMA(stock.getSymbol(), 50, stock.getClose());
							ema100 = calculateHistoricalEMA(stock.getSymbol(), 100, stock.getClose());
							ema150 = calculateHistoricalEMA(stock.getSymbol(), 150, stock.getClose());
							ema200 = calculateHistoricalEMA(stock.getSymbol(), 200, stock.getClose());
						}

					}
					
					//used to generate ema of a specific date
					/*Calendar calendar = stock.getDate();
					if(calendar.get(Calendar.DAY_OF_MONTH) == 12 && calendar.get(Calendar.MONTH)==0 && calendar.get(Calendar.YEAR) == 2018){
						System.out.println(calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR)+" ema5-"+ema5);
						break;
					}*/
			
					j++;
				}

				f[0] = ema3;
				f[1] = ema5;
				f[2] = ema10;
				f[3] = ema15;
				f[4] = ema30;
				f[5] = ema50;
				f[6] = ema100;
				f[7] = ema150;
				f[8] = ema200;

				map.put(s + ".NS", f);

				i++;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return map;
	}

	public static float calculateHistoricalEMA(String sym, int N, BigDecimal pToday) {
		float K = 2.0f / (N + 1.0f);
		float EMAyesterday = 0.0f;
		if (N == 3)
			EMAyesterday = ema3;
		if (N == 5)
			EMAyesterday = ema5;
		if (N == 10)
			EMAyesterday = ema10;
		if (N == 15)
			EMAyesterday = ema15;
		if (N == 30)
			EMAyesterday = ema30;
		if (N == 50)
			EMAyesterday = ema50;
		if (N == 100)
			EMAyesterday = ema100;
		if (N == 150)
			EMAyesterday = ema150;
		if (N == 200)
			EMAyesterday = ema200;

		float EMAtoday = (pToday.floatValue() * K) + (EMAyesterday * (1 - K));

		return EMAtoday;

	}

}
