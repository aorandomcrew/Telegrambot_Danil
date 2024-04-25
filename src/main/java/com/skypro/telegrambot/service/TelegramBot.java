package com.skypro.telegrambot.service;

import com.skypro.telegrambot.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component//автоматический экзмепляр спринга
public class TelegramBot extends TelegramLongPollingBot {

    final Config botConfig;

    static final String INFO_TEXT = "Какую информацию вы бы хотели узнать о приюте?";

    public TelegramBot(Config botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/info", "Узнать информацию о приюте"));
        listOfCommands.add(new BotCommand("/pet", "Как взять животное из приюта"));
        listOfCommands.add(new BotCommand("/report", "Прислать отчет о питомцее"));
        listOfCommands.add(new BotCommand("/help", "Позвать волонтера"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/info":
                    sendMessage(chatId, INFO_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Извините, неизвестная команда");
            }
        }


    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет," + name + "! Я приют-бот, я могу отвечать на популярные вопросы людей о том, что нужно знать и уметь, чтобы забрать животное из приюта";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }
}
