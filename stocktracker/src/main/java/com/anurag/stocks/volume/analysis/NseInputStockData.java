package com.anurag.stocks.volume.analysis;

public class NseInputStockData {

  String symbol;
  String series;
  float open;
  float high;
  float low;
  float close;
  float previousClose;
  long volume;
  long volume3mAvg;
  float deliveryVolPercent;
  float priceChange;
  String date;
  
  public String getSymbol() {
    return symbol;
  }
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  public float getOpen() {
    return open;
  }
  public void setOpen(float open) {
    this.open = open;
  }
  public float getHigh() {
    return high;
  }
  public void setHigh(float high) {
    this.high = high;
  }
  public float getLow() {
    return low;
  }
  public void setLow(float low) {
    this.low = low;
  }
  public float getClose() {
    return close;
  }
  public void setClose(float close) {
    this.close = close;
  }
  public long getVolume() {
    return volume;
  }
  public void setVolume(long volume) {
    this.volume = volume;
  }
  public long getVolume3mAvg() {
    return volume3mAvg;
  }
  public void setVolume3mAvg(long volume3mAvg) {
    this.volume3mAvg = volume3mAvg;
  }
  public float getPriceChange() {
    return priceChange;
  }
  public void setPriceChange(float priceChange) {
    this.priceChange = priceChange;
  }
  public float getDeliveryVolPercent() {
    return deliveryVolPercent;
  }
  public void setDeliveryVolPercent(float deliveryVolPercent) {
    this.deliveryVolPercent = deliveryVolPercent;
  }
  public float getPreviousClose() {
    return previousClose;
  }
  public void setPreviousClose(float previousClose) {
    this.previousClose = previousClose;
  }
  public String getDate() {
    return date;
  }
  public void setDate(String date) {
    this.date = date;
  }
  public String getSeries() {
    return series;
  }
  public void setSeries(String series) {
    this.series = series;
  }
}
