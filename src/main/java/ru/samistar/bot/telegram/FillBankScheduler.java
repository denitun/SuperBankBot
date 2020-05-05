package ru.samistar.bot.telegram;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.samistar.bot.modal.Bank;
import ru.samistar.bot.modal.Change;
import ru.samistar.bot.modal.Currency;
import ru.samistar.bot.repo.BankRepository;
import ru.samistar.bot.repo.ChangeRepository;
import ru.samistar.bot.repo.CurrencyRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FillBankScheduler {
    private List<Bank> banks = new ArrayList<>();

    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private ChangeRepository changeRepository;
    @Autowired
    private CurrencyRepository currencyRepository;


    @Scheduled(fixedDelay = 1000 * 60)// указанное количество времени(смотреть выше) в милисекундах
    public void fill() throws IOException {
        banks.clear();

        Document document = Jsoup.connect("https://ru.myfin.by/currency/moskva").get();

        Elements elements = document.getElementsByClass("tr-turn");

        System.out.println("elements = " + elements);

        for (int i = 0; i < elements.size(); i++) {
            String bankName = elements.get(i).getElementsByClass("bank_link").first().text();
            String dollarBuy = elements.get(i).getElementsByClass("USD").first().text();
            String dollarSell = elements.get(i).getElementsByClass("USD").last().text();
            String euroBuy = elements.get(i).getElementsByClass("EUR").first().text();
            String euroSell = elements.get(i).getElementsByClass("EUR").last().text();
// пока забить на дату String date = elements.get(i).getElementsByClass("curr_hid").last().text();

//TODO логика заплнения банков и курсов валют
            Bank bank = bankRepository.findByName(bankName);
            if (bank != null) {
                Change dollar = bank.getDollar();
                dollar.setBuy(Double.valueOf(dollarBuy));
                dollar.setSell(Double.valueOf(dollarSell));
                Change euro = bank.getEuro();
                euro.setBuy(Double.valueOf(euroBuy));
                euro.setSell(Double.valueOf(euroSell));
                changeRepository.save(dollar);
                changeRepository.save(euro);
            } else {
                List<Currency> currencies = (List<Currency>) currencyRepository.findAll();
                Change dollar = new Change(currencies.get(0), currencies.get(1), Double.valueOf(dollarSell), Double.valueOf(dollarBuy));
                Change euro = new Change(currencies.get(0), currencies.get(2), Double.valueOf(euroSell), Double.valueOf(euroBuy));
                bank = new Bank(bankName);
                bank.setDollar(dollar);
                bank.setEuro(euro);
                bankRepository.save(bank);
            }
        }
    }
    public List<Bank> getBanks() {
        return banks;
    }
}

