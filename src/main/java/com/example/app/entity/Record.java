package com.example.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "records")
public class Record implements Comparable<Record> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;
    @NotNull(message = "Please provide fuel type for the record.")
    @Column(name = "fuel_type")
    private String fuelType;
    @NotNull(message = "Please provide price per liter for the fuel")
    @Positive(message = "Price can't be negative value")
    @Column(name = "price")
    private BigDecimal pricePerLiter;
    @NotNull(message = "Please provide volume for the record")
    @Positive(message = "Volume can't be negative value")
    @Column(name = "volume")
    private double volume;
    @NotNull(message = "Please provide date for the record")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM.dd.yyyy")
    @Column(name = "date")
    private LocalDate date;
    @NotNull(message = "Please provide driver id for the record")
    @Column(name = "driver_id")
    private long driverId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(BigDecimal pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double liters) {
        this.volume = liters;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Double.compare(record.volume, volume) == 0 &&
                driverId == record.driverId &&
                fuelType.equals(record.fuelType) &&
                pricePerLiter.equals(record.pricePerLiter) &&
                date.equals(record.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fuelType, pricePerLiter, volume, date, driverId);
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", fuelType='" + fuelType + '\'' +
                ", pricePerLiter=" + pricePerLiter +
                ", volume=" + volume +
                ", date=" + date +
                ", driverId=" + driverId +
                '}';
    }

    @Override
    public int compareTo(Record record) {
        int result = this.date.compareTo(record.date);
        if (result == 0) {
            result = this.fuelType.compareTo(record.fuelType);
        }
        return result;
    }
}
