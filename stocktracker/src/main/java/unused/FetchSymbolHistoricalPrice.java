package unused;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class FetchSymbolHistoricalPrice {

	public static  List<HistoricalQuote> getHistoryData(String sym) throws IOException {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.DAY_OF_MONTH, -15); // for 15 days

		Stock hstock = YahooFinance.get(sym);
		List<HistoricalQuote> histQuotes = hstock.getHistory(from, to, Interval.DAILY);

		return histQuotes.subList(histQuotes.size() - 1 - 6, histQuotes.size());

	}

}
