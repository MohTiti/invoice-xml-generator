package com.xml.generation.test.invoice_xml_generator_test.signing.config;

import com.xml.generation.test.invoice_xml_generator_test.signing.service.impl.SigningServiceImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Configuration
@Order(1)
public class SigningConfig {

    @Value("${signing.private.key}")
    private String signingPrivateKey;

    @Value("${signing.certificate}")
    private String signingCertificateAsString;

    @Bean("signingPrivateKey")
    public PrivateKey getPrivateKey() throws Exception {
        try {
            Security.addProvider(new BouncyCastleProvider());
            String pem = "-----BEGIN EC PRIVATE KEY-----\n"
                    + signingPrivateKey.replaceAll("\n", "").replaceAll("\t", "")
                    + "\n-----END EC PRIVATE KEY-----";
            Reader reader = new InputStreamReader(
                    new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)));
            Object parsed = new PEMParser(reader).readObject();
            KeyPair pair = new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) parsed);
            return pair.getPrivate();
        } catch (Exception e) {
            CustomLogging.logError("PRIVATE_KEY_LOAD_FAILED", null,
                    "Failed to load signing private key — application cannot sign invoices: {}", e.getMessage());
            throw e;
        }
    }

    @Bean("signingCertificate")
    public X509Certificate getCertificate() throws Exception {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            byte[] certBytes = Base64.getDecoder().decode(signingCertificateAsString.getBytes(StandardCharsets.UTF_8));
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (Exception e) {
            CustomLogging.logError("CERTIFICATE_LOAD_FAILED", null,
                    "Failed to load signing certificate — application cannot sign invoices: {}", e.getMessage());
            throw e;
        }
    }

    @Bean("signingService")
    public SigningServiceImpl getSigningService(PrivateKey signingPrivateKey,
                                                X509Certificate signingCertificate) {
        SigningServiceImpl signingService = new SigningServiceImpl();
        signingService.setPrivateKey(signingPrivateKey);
        signingService.setCertificate(signingCertificate);
        signingService.setCertificateAsString(signingCertificateAsString);
        return signingService;
    }
}
