package com.api.gateway.utils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Service
@Slf4j
public class KeyLoader {

    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            this.publicKey = loadPublicKey("keys/public.pem");
            log.info("Public Key loaded successfully");
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load RSA keys", e);
        }
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        String key = readKey(path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        log.info("Public key Base64 decoded successfully");

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(spec);
    }

    private String readKey(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);

        if (!resource.exists()) {
            log.error("Key file NOT FOUND at path: {}", path);
            throw new RuntimeException("Key file not found: " + path);
        }
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
