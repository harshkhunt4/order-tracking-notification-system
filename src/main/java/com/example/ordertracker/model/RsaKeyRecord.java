package com.example.ordertracker.model;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record RsaKeyRecord(RSAPublicKey rsaPublicKey,RSAPrivateKey rsaPrivateKey) {

}
