package com.fisa.bank.common.config.security.jwt;

import lombok.Getter;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

@Getter
@ConfigurationProperties(prefix = "jwk")
public class JwkProperties {

  private final PrivateKey privateKey;
  private final PublicKey publicKey;
  private final String jwsAlgorithm;

  public JwkProperties(String publicKey, String privateKey) {
    //        this.privateKey = AsymmetricKeyUtils.createPrivateKey(privateKey, "RSA");
    //        this.publicKey = AsymmetricKeyUtils.createPublicKey(publicKey, "RSA");
    this.publicKey = null;
    this.privateKey = null;
    this.jwsAlgorithm = JwsAlgorithms.RS256;
  }
}
