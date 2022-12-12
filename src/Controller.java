import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import jsonclasses.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Controller {

    // pesquisar perfis

    public void search(TelegramBot bot, Update message, String apiUrl){

        try{
            if (message.inlineQuery().query().length() >= 4 && message.inlineQuery().query().contains("instagram.com") == false){

                    String query = message.inlineQuery().query();
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/search?query=" + query.replaceAll(" ", "%20") + "&limit=50")).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200){

                        Gson gson = new Gson();
                        Users json = gson.fromJson(response.body(), Users.class);
                        InlineQueryResult<InlineQueryResultArticle>[] createResults = new InlineQueryResult[json.users.size()];
                        int count = 0;
                        for (User user : json.users){

                            if (user.name.equals("")){user.name = user.username;}
                            createResults[count] = new InlineQueryResultArticle(user.pkId , user.name, user.profileUrl).thumbUrl(user.profilePicUrl).description(user.username);
                            count++;
                        }

                        bot.execute(new AnswerInlineQuery(message.inlineQuery().id(), createResults));

                    }else{
                        InlineQueryResultArticle createArticle = new InlineQueryResultArticle("0", "nenhum resultado encontrado", "nenhum usuario foi encontrado" ).description("nenhum usuario foi encontrado.").thumbUrl("https://thumbs.dreamstime.com/b/lupa-virada-s%C3%ADmbolo-n%C3%A3o-encontrado-bonito-e-s-mal-sucedido-122205900.jpg");
                        bot.execute(new AnswerInlineQuery(message.inlineQuery().id(), new InlineQueryResult[] {createArticle}));
                    }

            }else{
                InlineQueryResultArticle createArticle = new InlineQueryResultArticle("0", "nenhum resultado encontrado", "nenhum usuario foi encontrado" ).description("nenhum usuario foi encontrado.").thumbUrl("https://thumbs.dreamstime.com/b/lupa-virada-s%C3%ADmbolo-n%C3%A3o-encontrado-bonito-e-s-mal-sucedido-122205900.jpg");
                bot.execute(new AnswerInlineQuery(message.inlineQuery().id(), new InlineQueryResult[] {createArticle}));
            }

        } catch (Exception e){

            e.printStackTrace();
            InlineQueryResultArticle createArticle = new InlineQueryResultArticle("0", "nenhum resultado pode ser exibido", "o instagram não autorizou o bot a fazer a consulta." ).description("o instagram não autorizou o bot a fazer a consulta.").thumbUrl("https://cdn-icons-png.flaticon.com/512/682/682010.png");
            bot.execute(new AnswerInlineQuery(message.inlineQuery().id(), new InlineQueryResult[] {createArticle}));
        }
    }

    // coletar stories

    public void stories(TelegramBot bot , Update message , String apiUrl){

        try{

            String profileUrl = message.message().text();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/stories?profile_url=" + profileUrl)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){

                Gson gson = new Gson();
                Stories json = gson.fromJson( response.body(), Stories.class);

                for (Storie storie : json.stories){

                    if (storie.type.equals("foto")){

                        bot.execute(new SendPhoto(message.message().chat().id(), storie.url).caption("foto postada há: " + storie.postedTo));
                    }

                    else {

                        bot.execute(new SendVideo(message.message().chat().id(), storie.url).caption("video postado há: " + storie.postedTo));
                    }
                }

            }else{

                bot.execute(new SendMessage(message.message().chat().id(), "desculpe não consigo te enviar stories deste usuario."));
            }


        }

        catch (Exception e){

            e.printStackTrace();
            bot.execute(new SendMessage(message.message().chat().id(), "desculpe não consigo te enviar stories deste usuario."));
        }
    }

    // videos dos reels

    public void reels(TelegramBot bot , Update message, String apiUrl){

        try{

        String reelsUrl = message.message().text();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/reels?video_url=" + reelsUrl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200){

            Gson gson = new Gson();
            Reels reels = gson.fromJson(response.body() , Reels.class);
            String subtitle = reels.subtitle;

            if (reels.subtitle.length() > 200 ){subtitle = "legenda indisponivel para este video.";}
            SendVideo videoCreate = new SendVideo(message.message().chat().id(), reels.url).caption(subtitle).supportsStreaming(true);
            bot.execute(videoCreate);
        } else {

            bot.execute(new SendMessage(message.message().chat().id(), "desculpe não consigo te enviar esse video. \uD83E\uDEE0"));
        }


        }
        catch (Exception e){

            e.printStackTrace();
            bot.execute(new SendMessage(message.message().chat().id(), "desculpe não consigo te enviar esse video. \uD83E\uDEE0"));
        }
    }

    // enviar publicações

    public  void posts(TelegramBot bot, Update message , String apiUrl){

        try{

            String postUrl = message.message().text();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/post?post_url=" + postUrl)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){

                Gson gson = new Gson();
                Publications json = gson.fromJson(response.body(), Publications.class);
                String subtitle = json.subtitle;
                if (json.subtitle.length() > 200){subtitle = "legenda indisponivel para esta publicação.";}

                // publicação com uma unica foto

                if (json.totalFotos == 1 && json.totalVideos == 0){

                    SendPhoto createPhoto = new SendPhoto(message.message().chat().id(), json.medias.get(0).url).caption(subtitle);
                    bot.execute(createPhoto);
                }

                // publicação com um unico video

                else if (json.totalVideos == 1 && json.totalFotos == 0) {

                    SendVideo createVideo = new SendVideo(message.message().chat().id(), json.medias.get(0).url).caption(subtitle).supportsStreaming(true);
                    bot.execute(createVideo);
                }

                // publicação com varios midias

                else {

                    InputMedia<?>[] createMedias = new InputMedia[json.medias.size()];
                    int count = 0;
                    for (  Publication pub : json.medias){

                        if(pub.type.equals("foto")){

                            createMedias[count] = new InputMediaPhoto(pub.url);
                        } else {

                            createMedias[count] = new InputMediaVideo(pub.url);
                        } count++;
                    }

                    bot.execute(new SendMediaGroup(message.message().chat().id(), createMedias));
                }


            }

            else {
                bot.execute(new SendMessage(message.message().chat().id(), "desculpe não consigo te enviar esta publicação. \uD83E\uDEE0"));
            }
        }


        catch (Exception e){

            e.printStackTrace();
            bot.execute(new SendMessage(message.message().chat().id(), "desculpe não consigo te enviar esta publicação. \uD83E\uDEE0"));
        }
    }

}
