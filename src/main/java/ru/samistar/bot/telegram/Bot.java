package ru.samistar.bot.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.samistar.bot.modal.Bank;
import ru.samistar.bot.modal.Favorites;
import ru.samistar.bot.modal.User;
import ru.samistar.bot.repo.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Component // штука засовывающая класс в хранилище бинов
public class Bot extends TelegramLongPollingBot {

    @Autowired  // найти в хранилище бинов(классов работников) переменную того же типа что и данное поле
    private FillBankScheduler fillBankScheduler; // данное поле(ничего само по себе не делает)

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private ChangeRepository changeRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private UserRepository userRepository;


    @PostConstruct
    private void init() {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }



    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() == null) {
            // если месседж пустой то это нажатие кнопки под сообщением
            String s = update.getCallbackQuery().getData();
            System.out.println("s =" + s);
            s = s.replace("addToFavorites", "");
            int idishnik = Integer.parseInt(s);
            Bank bank = bankRepository.findById(idishnik).get();
            System.out.println("Bank =" + bank);
            int userId = update.getCallbackQuery().getFrom().getId();
            User user = userRepository.findByChatId(userId);
            Favorites favorites = new Favorites(user, bank);
            favoritesRepository.save(favorites);
        }else{
            String message = update.getMessage().getText();
            if (message.equals("Курс Валюты")) {
                message = bankRepository.findById(3).get().toString();
                sendMsg(update.getMessage().getChatId().toString(), message, null);
            } else if (message.equals("Поиск")) {
                message = "Для поиска банка пришлите часть его имени";
                sendMsg(update.getMessage().getChatId().toString(), message, null);
            } else { // элс который ищет банк по подстроке(части строки) в имени
                List<Bank> banks = bankRepository.findByNameContainsIgnoreCase(message);
                if (banks.isEmpty()) {
                    sendMsg(update.getMessage().getChatId().toString(), "Не найдено", null);
                }
            }
        }
    }

    /**
     * Метод для настройки сообщения и его отправки.
     *
     * @param chatId id чата
     * @param s      Строка, которую необходимот отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String s, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        setButtons(sendMessage);
        if(inlineKeyboardMarkup != null){
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "project_project_bot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "1120734227:AAFMoWfTTbEwYyoEbIm8cvY4lb_AeXC55BY";
    }


    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Курс Валюты"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("Банки"));

        // Третья строчка клавиатуры
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        // Добавляем кнопки в третью строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Обменники"));

        // Четвёртая строчка клавиатуры
        KeyboardRow keyboardFourthRow = new KeyboardRow();
        // Добавляем кнопки в четвёртую строчку клавиатуры
        keyboardFourthRow.add(new KeyboardButton("Настройки"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        keyboard.add(keyboardFourthRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}

