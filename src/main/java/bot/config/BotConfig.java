package bot.config;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${access.token}")
    private String accessToken;
    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;

    private OAuth2Credential credential;

    @Bean
    public TwitchClient client() {
        credential = new OAuth2Credential("twitch", accessToken);
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableHelix(true)
                .withChatAccount(credential)
                .withEnableChat(true)
                .build();

        twitchClient.getClientHelper().enableStreamEventListener("raccoona_gg");
        twitchClient.getChat().joinChannel("raccoona_gg");
        return twitchClient;
    }

}
