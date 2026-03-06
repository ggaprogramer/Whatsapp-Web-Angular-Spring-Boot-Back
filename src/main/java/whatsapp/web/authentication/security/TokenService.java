package whatsapp.web.authentication.security;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import whatsapp.web.authentication.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private RSAKey rsaKey;

    public String generateToken(Usuario user, Integer expirationToken) throws Exception {
        if(this.rsaKey == null){
            this.rsaKey = gerarChaveRSA();
        }
        JWKSet jwkSet = new JWKSet(this.rsaKey);

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(expirationToken, ChronoUnit.DAYS))
                .claim("idUser", user.getId())
                .build();

        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    // Gerar par de chaves RSA
    private RSAKey gerarChaveRSA() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String keyId = UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString();

        return new RSAKey
                .Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();
    }

    public String validateToken(String token) {
        try {
            // Parseia o token JWT
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Extrai a chave pública do RSAKey
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            // Verifica a assinatura do token
            if (!signedJWT.verify(verifier)) {
                return null;
            }

            // Recupera o claim "idUser" do payload
            String idUser = signedJWT.getJWTClaimsSet().getStringClaim("idUser");

            if (idUser == null || idUser.isEmpty()) {
                return null;
            }

            return idUser;

        } catch (Exception e) {
            return null;
        }
    }

}
