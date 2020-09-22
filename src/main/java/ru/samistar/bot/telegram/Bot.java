package ru.samistar.bot.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
//дофига библиотек


@Component // штука засовывающая класс в хранилище бинов
public class Bot extends TelegramLongPollingBot {

    @Autowired  // найти в хранилище бинов(классов работников) переменную того же типа что и данное поле
    private FillBankScheduler fillBankScheduler; // данное поле(ничего само по себе не делает)


    //подключаем репозитории к этому классу
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

//???
    @PostConstruct
    private void init() {
//        System.getProperties().put("proxySet", "true");
//        System.getProperties().put("socksProxyHost", "127.0.0.1");
//        System.getProperties().put("socksProxyPort", "9150");


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
    public void onUpdateReceived(Update update) { //избранное

        if (update.getMessage() == null) {    // если месседж пустой то это нажатие кнопки под сообщением


            User user2 = new User(update.getCallbackQuery().getFrom().getId());
            if(userRepository.findByChatId(update.getCallbackQuery().getFrom().getId()) == null){
                userRepository.save(user2);
            }


            String s = update.getCallbackQuery().getData(); // строка которую пишет юзер в телеграм
            System.out.println("s =" + s); // вывод на экран компа этой строки
            s = s.replace("addToFavorites", ""); // удаляет из строки addToFavorites
            int idishnik = Integer.parseInt(s); // достаёт айди банка из строчки s
            Bank bank = bankRepository.findById(idishnik).get(); // лезет в бд и достаёт оттуда банк с нужным айди
            System.out.println("Bank =" + bank); // выводит на экран этот банк
            int userId = update.getCallbackQuery().getFrom().getId(); // достает айди юзера
            User user = userRepository.findByChatId(userId); // лезет в бд и достаёт оттуда юзера с нужным айди
            Favorites favorites = new Favorites(user, bank); // создает объект c нужным юзером и банком

            String textButtonCheck = update.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard().get(0).get(0).getText();
            if(textButtonCheck.startsWith("Удалить из избранного") == true);{
                favoritesRepository.deleteByUserChatIdAndBankId(favorites);

            }
            else{
                favoritesRepository.save(favorites); // сохраняет его в бд
            }
        } else {
            String message = update.getMessage().getText(); // берем текст сообщения
            if (message.equals("Курс Валюты")) { // если юзер написал "курс валюты"
                message = bankRepository.findById(3).get().toString(); // берет из репозитория курс валюты всех банков и преобразует его в строчку
                sendMsg(update.getMessage().getChatId().toString(), message, null); // вызывает метод сендМесседж выводя все банки в телеграм
            } else if (message.equals("Поиск")) { // если юзер написал "поиск"
                message = "Для поиска банка пришлите часть его имени"; // создаем строчку с  текстом "Для поиска банка пришлите часть его имени"
                sendMsg(update.getMessage().getChatId().toString(), message, null); // вызывает метод сендМесседж выводя эту строчку на экран в телеграм
            } else { // элс который ищет банк по подстроке(части строки) в имени
                List<Bank> banks = bankRepository.findByNameContainsIgnoreCase(message); //берёт из репозитория все банки с именем содержащим набранный юзером в телеграме текст(не обращая внимания на размер букв)
                if (banks.isEmpty()) { // если таких банков нет то
                    sendMsg(update.getMessage().getChatId().toString(), "Не найдено", null); // вызываем метод сендмесседж выводя в телеграмм "не найдено"
                }


                for (int i = 0; i < banks.size(); i++) {
                    //Создаем обьект разметки клавиатуры:
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                    //Создаем обьект InlineKeyboardButton, у которой есть 2 параметка: Текст (Что будет написано на самой кнопке) и CallBackData (Что будет отсылатся серверу при нажатии на кнопку).

                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();// создаём кнопу "добавить в избранное"
                    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();// создаём кнопку "удалить из избранного"




                    // Добавляем его в список, таким образом создавая ряд.
                    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                    List<List<InlineKeyboardButton>> keyboardButtonsRows = new ArrayList<>();


                    if(favoritesRepository.findByUserChatIdAndBankId(update.getMessage().getFrom().getId(), banks.get(i).getId()) == null){
                        inlineKeyboardButton.setCallbackData(banks.get(i).getId().toString());
                        inlineKeyboardButton.setText("Добавить в избранное");
                        keyboardButtonsRow1.add(inlineKeyboardButton);
                        keyboardButtonsRows.add(keyboardButtonsRow1);
                    }
                    else{
                        inlineKeyboardButton2.setCallbackData(banks.get(i).getId().toString());
                        inlineKeyboardButton2.setText("Удалить из избранного");
                        keyboardButtonsRow1.add(inlineKeyboardButton2);
                        keyboardButtonsRows.add(keyboardButtonsRow1);
                    }
                    inlineKeyboardMarkup.setKeyboard(keyboardButtonsRows);

                    sendMsg(update.getMessage().getChatId().toString(), banks.get(i).toString(), inlineKeyboardMarkup);
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
    public synchronized void sendMsg(String chatId, String s, InlineKeyboardMarkup inlineKeyboardMarkup) { // метод отправляющий сообщения юзеру в телеграмм
        SendMessage sendMessage = new SendMessage(); // создает объект сендмесседж
        sendMessage.enableMarkdown(true); //
        sendMessage.setChatId(chatId); // добавляет ему айди чата
        sendMessage.setText(s); // добавляет ему текст содержащийся в S
        setButtons(sendMessage); // добавляем ему кнопки

        if (inlineKeyboardMarkup != null) { //
            sendMessage.setReplyMarkup(inlineKeyboardMarkup); //

        }
        try { //
            execute(sendMessage); //выполняет этод метод?
        } catch (TelegramApiException e) {//
            e.printStackTrace();//
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

