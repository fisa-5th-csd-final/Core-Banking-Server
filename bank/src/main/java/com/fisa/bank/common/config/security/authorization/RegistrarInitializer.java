package com.fisa.bank.common.config.security.authorization;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegistrarInitializer implements ApplicationRunner {

    private final RegisteredClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    // RegistrarClient 정보
    private final String clientId;
    private final String secret;

    public RegistrarInitializer(
            RegisteredClientRepository clientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${oauth2.registrar-client.id}") String clientId,
            @Value("${oauth2.registrar-client.secret}") String secret
    ){
        this.clientId = clientId;
        this.secret = secret;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (clientRepository.findByClientId(clientId) == null) {
            RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(passwordEncoder.encode(secret))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .scope("client.create")
                    .scope("client.read")
                    .build();
            clientRepository.save(client);
            log.info("Registrar Client를 추가하였습니다.");
        } else {
            log.info("Registrar Client가 이미 존재합니다. ");
        }
    }
}
