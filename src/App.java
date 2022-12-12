import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jsonclasses.Configs;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class App {
    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Bot iniciando com sucesso, aguardando solicitações");
        // pegar a chave da API no config.json e o link da APIinstagram depois instanciar o bot

        Gson json = new Gson();
        Configs configs_json =  json.fromJson(new FileReader("Configs.json"), Configs.class);
        String apiUrl = configs_json.apiUrl;
        String telegramToken = configs_json.telegramApiToken;


        TelegramBot bot = new TelegramBot(telegramToken);

        // receber atualizações

        bot.setUpdatesListener(

                messages -> {

                    for (Update message : messages){

                        if (message.message() != null && message.message().text() != null) {

                            // mensagem de boas-vindas

                            if (message.message().text().equals("/start")){

                                InlineKeyboardMarkup button = new InlineKeyboardMarkup(new InlineKeyboardButton("encontrar perfil").switchInlineQueryCurrentChat(""));
                                bot.execute(new SendMessage(message.message().chat().id(), "olá, por favor me envie o link de um perfil, publicação ou video do reels para que eu possa te enviar tudo! \uD83E\uDEE3").replyMarkup(button));
                            }

                            // gerenciar consultas para videos do reels

                            else if (message.message().text().contains(".instagram.com/reel/")){

                                System.out.println("nova consulta para video do reels.");
                                SendMessage createMessage = new SendMessage(message.message().chat().id(), "buscando informações do video, aguarde...").replyToMessageId(message.message().messageId());
                                bot.execute(createMessage);
                                Controller insta = new Controller();
                                insta.reels(bot , message, apiUrl);
                            }
                            // gerenciar consultas para publicações

                            else if (message.message().text().contains("instagram.com/p/")) {

                                System.out.println("nova consulta para publicação.");
                                SendMessage createMessage = new SendMessage(message.message().chat().id(), "buscando informações da publicação, aguarde...").replyToMessageId(message.message().messageId());
                                bot.execute(createMessage);
                                Controller insta = new Controller();
                                insta.posts(bot, message, apiUrl);
                            }

                            // gerenciar consultas para stories

                            else if (message.message().text().contains("instagram.com")) {

                                if (message.message().text().contains("?igshid=true")){

                                    SendMessage createMessage = new SendMessage(message.message().chat().id(), "este usuario tem o perfil privado, impossivel pegar stories. \uD83E\uDEE0").replyToMessageId(message.message().messageId());
                                    bot.execute(createMessage);

                                }else {
                                    System.out.println("nova consulta para stories.");
                                    SendMessage createMessage = new SendMessage(message.message().chat().id(), "buscando stories do perfil, aguarde...").replyToMessageId(message.message().messageId());
                                    bot.execute(createMessage);
                                    Controller insta = new Controller();
                                    insta.stories(bot, message, apiUrl);
                                }}

                            // o usuario não enviou um link do instagram

                            else {

                                SendMessage createMessage = new SendMessage(message.message().chat().id(), "ue? eu só posso processar links do instagram. \uD83E\uDEE4 ");
                                bot.execute(createMessage);
                            }

                        } else if (message.inlineQuery() != null && message.inlineQuery().id() != null) {

                            Controller insta = new Controller();
                            insta.search(bot, message, apiUrl);
                        }


                    } return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }

        );
    }

}