package com.anurag.stocks.model;

public class NSEFullData {

  String symbol;
  String date;
  String series;
  float prev_close;
  float open;
  float high;
  float low;
  float close;
  long tol_trd_qty;
  long del_qty;
  float del_percent;

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public float getPrev_close() {
    return prev_close;
  }

  public void setPrev_close(float prev_close) {
    this.prev_close = prev_close;
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

  public long getTol_trd_qty() {
    return tol_trd_qty;
  }

  public void setTol_trd_qty(long tol_trd_qty) {
    this.tol_trd_qty = tol_trd_qty;
  }

  public long getDel_qty() {
    return del_qty;
  }

  public void setDel_qty(long del_qty) {
    this.del_qty = del_qty;
  }

  public float getDel_percent() {
    return del_percent;
  }

  public void setDel_percent(float del_percent) {
    this.del_percent = del_percent;
  }

  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public float getOpen() {
    return open;
  }

  public void setOpen(float open) {
    this.open = open;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

}
