package ru.samistar.bot.modal;

public class Change {
    private Currency first;
    private Currency second;
    private Double sell;
    private Double buy;

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
}
