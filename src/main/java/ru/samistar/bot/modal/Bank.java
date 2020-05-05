package ru.samistar.bot.modal;

import javax.persistence.*;

@Entity
public class Bank {
    private String name;

    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(cascade = CascadeType.ALL)
    private Change dollar;
    @OneToOne(cascade = CascadeType.ALL)
    private Change euro;

    public Bank(String name) {
        this.name = name;
    }

    public Bank(String name, Double dollarSell, Double dollarBuy, Double euroSell, Double euroBuy) {
        Currency rub = new Currency("Рубль");
        Currency euro = new Currency("Евро");
        Currency dollar = new Currency("Доллар");
        Change rubToDollar = new Change(rub, dollar, dollarSell, dollarBuy);
        Change rubToEuro = new Change(rub, euro, euroSell, euroBuy);
        this.dollar = rubToDollar;
        this.euro = rubToEuro;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "name='" + name + '\'' +
                ", dollar=" + dollar +
                ", euro=" + euro +
                '}';
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Change getDollar() {
        return dollar;
    }

    public void setDollar(Change dollar) {
        this.dollar = dollar;
    }

    public Change getEuro() {
        return euro;
    }

    public void setEuro(Change euro) {
        this.euro = euro;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
