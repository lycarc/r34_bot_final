import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import reactor.core.publisher.Mono;

import java.io.IOException;

public class Main
    {
        public static void main(String[] args)
            {
                DiscordClient client = DiscordClient.create("OTkwOTE3NjY3ODg2MDcxODQ4.GJ6f5J.QlNfpwyOpNnMkVjKXV8Nm3qc9UXClk54tRaxDs");

                Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) ->
                        {
                            // ReadyEvent example
                            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                                Mono.fromRunnable(() ->
                                    {
                                        final User self = event.getSelf();
                                        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                                    })).then();

                            //Search command
                            Mono<Void> searchR34 = gateway.on(MessageCreateEvent.class, event ->
                            {
                                Message message = event.getMessage();

                                if (message.getContent().contains("!search"))
                                    {
                                        return message.getChannel().flatMap(channel -> channel.createMessage(search(message.getContent().substring(8)) + "posts under " + message.getContent().substring(8)));
                                    }

                                return Mono.empty();

                            }).then();

                            return printOnLogin.and(searchR34);

                        });
                login.block();
            }

        public static String search(String tag)
            {
                tag = tag.toLowerCase();
                try
                    {
                        Document doc = Jsoup.connect("https://rule34.xxx/index.php?page=tags&s=list&tags=" + tag).get();
                        String html = doc.text();

                        int t1 = html.indexOf("Name Type") + 9;
                        int t2 = html.indexOf(tag);
                        html = html.substring(t1, t2);
                        return html;
                    }

                catch (IOException ex)
                    {
                        return "uh oh, poopy";
                    }
            }
    }