package com.xml.generation.test.invoice_xml_generator_test.signing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
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

/**
 * Loads the EC private key and X.509 certificate from application properties,
 * then wires them into the embedded SigningServiceImpl bean.
 *
 * Properties required in application.yaml:
 *   signing.private.key  — raw EC private key PEM body (no header/footer lines)
 *   signing.certificate  — base64-encoded DER X.509 certificate
 */
@Configuration
@Order(1)
public class SigningConfig {

    /**
     * EC private key PEM body, without the BEGIN/END header lines.
     * Example: "MHQCAQEEIFac9R0XfZiC/..." (the raw base64 lines of the PEM)
     */
    @Value("${signing.private.key}")
    private String signingPrivateKey;

    /**
     * Base64-encoded DER X.509 certificate (the PEM body without header/footer).
     */
    @Value("${signing.certificate}")
    private String signingCertificateAsString;

    @Bean("signingPrivateKey")
    public PrivateKey getPrivateKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        // Reconstruct full PEM by wrapping the raw body with EC headers
        String pem = "-----BEGIN EC PRIVATE KEY-----\n"
                + signingPrivateKey.replaceAll("\n", "").replaceAll("\t", "")
                + "\n-----END EC PRIVATE KEY-----";
        Reader reader = new InputStreamReader(
                new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)));
        Object parsed = new PEMParser(reader).readObject();
        KeyPair pair = new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) parsed);
        return pair.getPrivate();
    }

    @Bean("signingCertificate")
    public X509Certificate getCertificate() throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        byte[] certBytes = Base64.getDecoder().decode(signingCertificateAsString.getBytes(StandardCharsets.UTF_8));
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
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
