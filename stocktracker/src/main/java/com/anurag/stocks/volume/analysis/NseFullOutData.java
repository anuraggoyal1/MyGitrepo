package com.anurag.stocks.volume.analysis;

public class NseFullOutData {

  String symbol;
  String date;
  float open;
  float high;
  float low;
  float close;
  float priceChange;
  long today_traded_qty;
  long avg_delivery_qty;
  long today_delivery_qty;
  float deliveryVolChangeInPercent;
  float netDeliveryToTradedVolInPercent;
  float up_down_del_vol_ratio;
  
  
  public String getSymbol() {
    return symbol;
  }
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  public String getDate() {
    return date;
  }
  public void setDate(String date) {
    this.date = date;
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
  public float getPriceChange() {
    return priceChange;
  }
  public void setPriceChange(float priceChange) {
    this.priceChange = priceChange;
  }
  public long getToday_traded_qty() {
    return today_traded_qty;
  }
  public void setToday_traded_qty(long today_traded_qty) {
    this.today_traded_qty = today_traded_qty;
  }
  public long getAvg_delivery_qty() {
    return avg_delivery_qty;
  }
  public void setAvg_delivery_qty(long avg_delivery_qty) {
    this.avg_delivery_qty = avg_delivery_qty;
  }
  public long getToday_delivery_qty() {
    return today_delivery_qty;
  }
  public void setToday_delivery_qty(long today_delivery_qty) {
    this.today_delivery_qty = today_delivery_qty;
  }
  public float getDeliveryVolChangeInPercent() {
    return deliveryVolChangeInPercent;
  }
  public void setDeliveryVolChangeInPercent(float deliveryVolChangeInPercent) {
    this.deliveryVolChangeInPercent = deliveryVolChangeInPercent;
  }
  public float getNetDeliveryToTradedVolInPercent() {
    return netDeliveryToTradedVolInPercent;
  }
  public void setNetDeliveryToTradedVolInPercent(float netDeliveryToTradedVolInPercent) {
    this.netDeliveryToTradedVolInPercent = netDeliveryToTradedVolInPercent;
  }
  public float getUp_down_del_vol_ratio() {
    return up_down_del_vol_ratio;
  }
  public void setUp_down_del_vol_ratio(float up_down_del_vol_ratio) {
    this.up_down_del_vol_ratio = up_down_del_vol_ratio;
  }
}
