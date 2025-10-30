package com.fisa.bank.common.config.security.jwk;

import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwk")
public class JwkProperties {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwkProperties(String publicKey, String privateKey){
//        this.privateKey = AsymmetricKeyUtils.createPrivateKey(privateKey, "RSA");
//        this.publicKey = AsymmetricKeyUtils.createPublicKey(publicKey, "RSA");
        this.publicKey = null;
        this.privateKey = null;
    }

}
