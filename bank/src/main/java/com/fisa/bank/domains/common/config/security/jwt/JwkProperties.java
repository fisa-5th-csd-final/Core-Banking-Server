package com.fisa.bank.domains.common.config.security.jwt;

import lombok.Getter;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

import com.fisa.bank.domains.common.config.security.util.AsymmetricKeyUtils;
import com.fisa.bank.domains.common.config.security.util.Readers;

@Getter
@ConfigurationProperties(prefix = "jwk")
public class JwkProperties {

  private final PrivateKey privateKey;
  private final PublicKey publicKey;
  private final String jwsAlgorithm;

  public JwkProperties(String publicKeyPath, String privateKeyPath) {
    this.publicKey = AsymmetricKeyUtils.createPublicKey(Readers.readFromFile(publicKeyPath), "RSA");
    this.privateKey =
        AsymmetricKeyUtils.createPrivateKey(Readers.readFromFile(privateKeyPath), "RSA");
    this.jwsAlgorithm = JwsAlgorithms.RS256;
  }
}
