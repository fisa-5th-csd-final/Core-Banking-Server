package com.fisa.bank.common.config.security.authorization;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.oidc.converter.OidcClientRegistrationRegisteredClientConverter;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

public class OidcClientRegistrationConverter
    implements Converter<OidcClientRegistration, RegisteredClient> {

  private final OidcClientRegistrationRegisteredClientConverter delegate;

  public OidcClientRegistrationConverter(OidcClientRegistrationRegisteredClientConverter delegate) {
    this.delegate = delegate;
  }

  @Override
  public RegisteredClient convert(OidcClientRegistration source) {

    RegisteredClient base = delegate.convert(source);
    // 클라이언트가 보낸 require_proof_key(불리언)를 그대로 존중
    Object raw = source.getClaims().get("require_proof_key");

    boolean reqPkce = (raw instanceof Boolean b) ? b : false; // 값이 없으면 기본 false(서버 앱에 유리)

    ClientSettings newSettings =
        ClientSettings.withSettings(base.getClientSettings().getSettings())
            .requireProofKey(reqPkce)
            .build();

    return RegisteredClient.from(base).clientSettings(newSettings).build();
  }
}
