package com.stefanantal.dto;

import java.time.LocalDate;

public class Release {

  private String region;
  private LocalDate date;

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}
