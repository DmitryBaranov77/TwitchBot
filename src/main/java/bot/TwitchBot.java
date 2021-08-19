package bot;

import bot.services.SendInfoMessage;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@PropertySource("classpath:application.properties")
public class TwitchBot{
    private TwitchClient twitchClient;
    private ScheduledExecutorService se;

    public TwitchBot(TwitchClient twitchClient) {
        this.twitchClient = twitchClient;
        register();
    }

    private void register(){
        EventManager eventManager = twitchClient.getEventManager();
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
        eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
        eventHandler.onEvent(ChannelGoLiveEvent.class, event -> onLiveEvent(event));
        eventHandler.onEvent(ChannelGoOfflineEvent.class, event -> onOfflineEvent(event));
    }

    private void onLiveEvent(ChannelGoLiveEvent event){
        twitchClient.getChat().sendMessage(event.getChannel().getName(), "Удачного стрима красотка!");
        se.scheduleAtFixedRate(new SendInfoMessage(twitchClient, event.getChannel().getName()), 0, 5, TimeUnit.MINUTES);
    }

    private void onOfflineEvent(ChannelGoOfflineEvent event){
        twitchClient.getChat().sendMessage(event.getChannel().getName(), "Пока пока");
        se.shutdown();
        twitchClient.getChat().leaveChannel(event.getChannel().getName());
    }

    private void onChannelMessage(ChannelMessageEvent event){
        String msg = event.getMessage();
        switch (msg) {
            case "а":
                event.getTwitchChat().sendMessage(event.getChannel().getName(), event.getUser().getName() + " хуй на");
                break;
            case "!start":
                se = Executors.newScheduledThreadPool(1);
                se.scheduleAtFixedRate(new SendInfoMessage(twitchClient, event.getChannel().getName()), 0, 5, TimeUnit.MINUTES);
                break;
            case "!stop":
                se.shutdown();
                break;
            case "!см":
                event.getTwitchChat().sendMessage(event.getChannel().getName(), event.getUser().getName()+" Размер твоего меча Экскалибура "+
                        ( (int) (1 + Math.random() * 30))+" см.");
                break;
        }
    }

}
