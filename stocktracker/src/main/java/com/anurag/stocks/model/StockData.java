package com.anurag.stocks.model;

public class StockData {

	String symbol;
	long volume;
	float priceChange;

	public StockData(String symbol, long vol, float priceChange) {
		this.symbol = symbol;
		this.volume = vol;
		this.priceChange = priceChange;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public float getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(float priceChange) {
		this.priceChange = priceChange;
	}
}
