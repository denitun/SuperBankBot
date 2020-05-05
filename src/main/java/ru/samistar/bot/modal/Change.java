package ru.samistar.bot.modal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Change {
    @OneToOne
    private Currency first;
    @OneToOne
    private Currency second;
    private Double sell;
    private Double buy;

    @Id
    @GeneratedValue
    private Integer id;

    public Change() {
    }

    public Change(Currency first, Currency second, Double sell, Double buy) {
        this.first = first;
        this.second = second;
        this.sell = sell;
        this.buy = buy;
    }

    @Override
    public String toString() {
        return "Change{" +
                "first=" + first +
                ", second=" + second +
                ", sell=" + sell +
                ", buy=" + buy +
                '}';
    }

    public Currency getFirst() {
        return first;
    }

    public void setFirst(Currency first) {
        this.first = first;
    }

    public Currency getSecond() {
        return second;
    }

    public void setSecond(Currency second) {
        this.second = second;
    }

    public Double getSell() {
        return sell;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    public Double getBuy() {
        return buy;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
