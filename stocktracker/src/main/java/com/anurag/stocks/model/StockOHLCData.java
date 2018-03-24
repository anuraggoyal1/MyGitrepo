package com.anurag.stocks.model;

public class StockOHLCData {

	String symbol;
	String date;
	float open;
	float high;
	float low;
	float close;
	float ema3d;
	float ema5d;
	float ema10d;
	float ema13d;
	float ema30d;
	float ema50d;
	float ema100d;
	float ema150d;
	float ema200d;

	public StockOHLCData(String symbol,  String date, float open, float high,
			float low, float close, float ema3d, float ema5d, float ema10d, float ema13d,
			float ema30d, float ema50d, float ema100d, float ema150d, float ema200d) {
		super();
		this.symbol = symbol;
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	
		this.ema3d = ema3d;
		this.ema5d = ema5d;
		this.ema10d = ema10d;
		this.ema13d = ema13d;
		this.ema30d = ema30d;
		this.ema50d = ema50d;
		this.ema100d = ema100d;
		this.ema150d = ema150d;
		this.ema200d = ema200d;
	}

	public String getDate() {
		return date;
	}

	public String getSymbol() {
		return symbol;
	}

	

	public float getOpen() {
		return open;
	}

	public float getHigh() {
		return high;
	}

	public float getLow() {
		return low;
	}

	public float getClose() {
		return close;
	}

	

	public float getEma3d() {
		return ema3d;
	}

	public float getEma5d() {
		return ema5d;
	}

	public float getEma10d() {
		return ema10d;
	}

	public float getEma13d() {
		return ema13d;
	}

	public float getEma30d() {
		return ema30d;
	}

	public float getEma50d() {
		return ema50d;
	}

	public float getEma100d() {
		return ema100d;
	}

	public float getEma150d() {
		return ema150d;
	}

	public float getEma200d() {
		return ema200d;
	}

	

}
