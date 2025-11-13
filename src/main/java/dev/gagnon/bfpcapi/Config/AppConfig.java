package dev.gagnon.bfpcapi.Config;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.modelmapper.Conditions.isNotNull;
import static org.modelmapper.convention.MatchingStrategies.STRICT;


@Configuration
public class AppConfig {
    @Value("${mailgun.api.key}")
    private String privateKey;
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(STRICT)
                .setPropertyCondition(isNotNull());
        return modelMapper;
    }
    @Bean
    public MailgunMessagesApi mailgunMessagesApi() {
        return MailgunClient.config(privateKey)
                .createApi(MailgunMessagesApi.class);
    }

}
