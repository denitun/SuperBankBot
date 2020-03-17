package ru.samistar.bot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.samistar.bot.modal.Bank;
import ru.samistar.bot.telegram.Bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BotApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(BotApplication.class, args);
		fill();
		banks.forEach(System.out::println);

		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "socksProxyHost", "127.0.0.1" );
		System.getProperties().put( "socksProxyPort", "9150" );
		ApiContextInitializer.init();

		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new Bot());
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
		}

	}
	static List<Bank> banks = new ArrayList<>();

	//    fixedDelay = 10_000
	public static void fill() throws IOException {
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
//Double kurs = Double.valueOf(euroBuy);
//


			Bank bank = new Bank(bankName, Double.valueOf(dollarSell), Double.valueOf(dollarBuy), Double.valueOf(euroSell), Double.valueOf(euroBuy));
			banks.add(bank);
			//System.out.println(bankName + " " + dollarBuy + " " + dollarSell + " " + euroBuy + " " + euroSell + " " + date);
		}
	}


}
