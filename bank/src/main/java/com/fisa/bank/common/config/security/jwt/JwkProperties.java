package com.fisa.bank.common.config.security.jwt;

import com.fisa.bank.common.config.security.util.AsymmetricKeyUtils;
import com.fisa.bank.common.config.security.util.Readers;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

@Getter
@ConfigurationProperties(prefix = "jwk")
public class JwkProperties {

  private final PrivateKey privateKey;
  private final PublicKey publicKey;
  private final String jwsAlgorithm;

  public JwkProperties(String publicKeyPath, String privateKeyPath) {
      this.publicKey = AsymmetricKeyUtils.createPublicKey(Readers.readFromFile(publicKeyPath), "RSA");
      this.privateKey = AsymmetricKeyUtils.createPrivateKey(Readers.readFromFile(privateKeyPath), "RSA");
      this.jwsAlgorithm = JwsAlgorithms.RS256;
  }
}
