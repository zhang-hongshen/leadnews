package com.zhanghongshen.utils;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@AllArgsConstructor
@Getter
public enum JwtUtils {

    INVALID(0, false),
    EXPIRED(1, false),
    REFRESH_NEEDED(2, true),
    VALID(3, true);

    private final int code;
    private final boolean valid;

    // token validity period: one day
    private static final int TOKEN_TIME_OUT = 86_400_000;
    // token encryption key
    private static final String SECRET_KEY = "thisIsA32ByteLongSecretKeyForHmac256";
    // refresh time: 5 minutes
    private static final int REFRESH_TIME = 300_000;


    public static String createToken(Long id){
        return createToken(id.toString());
    }

    public static String createToken(String id){
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", id);

        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .claims()
                .id(UUID.randomUUID().toString())
                .subject("system")
                .issuer("zhanghongshen")
                .issuedAt(new Date(currentTime))
                .audience().add("app").and()
                .expiration(new Date(currentTime + TOKEN_TIME_OUT))
                .add(claimsMap).and()
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 由字符串生成加密key
     *
     * @return signing key
     */
    private static SecretKey signingKey() {
        byte[] key = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(key, 0, key.length, "HmacSHA256");
    }

    /**
     * extract Claims from token
     *
     */
    private static Jws<Claims> parseJws(String token) {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token);
    }

    /**
     * extract payload from token
     *
     * @return token's payload
     */
    public static Claims getPayload(String token) {
        return parseJws(token).getPayload();
    }

    /**
     * extract header from token
     *
     * @return token's header
     */
    public static JwsHeader getHeader(String token) {
        return parseJws(token).getHeader();
    }

    /**
     * Check whether the token is valid
     *
     * @return 0：invalid token
     *         1: expired token
     *         2：token is needed to be refreshed
     *         3: token is not expired
     */
    public static JwtUtils validateToken(String token) {
        if(StringUtils.isBlank(token)) {
            return INVALID;
        }
        Claims claims = JwtUtils.getPayload(token);
        if(claims == null || claims.getExpiration() == null){
            return INVALID;
        }
        long diff = claims.getExpiration().getTime() - System.currentTimeMillis();
        if(diff > REFRESH_TIME){
            return EXPIRED;
        } else if(diff > 0) {
            return REFRESH_NEEDED;
        }
        return VALID;
    }


    public static void main(String[] args) {
        String token = JwtUtils.createToken(1102L);
        System.out.println("token " + token);
        Jws<Claims> jws = JwtUtils.parseJws(token);
        Claims claims = jws.getPayload();
        System.out.println(claims.get("id"));
    }

}
