package com.example.app.entity;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Objects;

public class RecordStatistics implements Comparable<RecordStatistics> {
    private Month month;
    private String fuelType;
    private double volume;
    private BigDecimal averagePrice;
    private BigDecimal totalPrice;

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordStatistics that = (RecordStatistics) o;
        return month == that.month &&
                fuelType.equals(that.fuelType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, fuelType);
    }


    @Override
    public int compareTo(RecordStatistics recordStatistics) {
        int result = this.month.compareTo(recordStatistics.month);
        if (result == 0) {
            result = this.fuelType.compareTo(recordStatistics.fuelType);
        }
        return result;
    }
}
