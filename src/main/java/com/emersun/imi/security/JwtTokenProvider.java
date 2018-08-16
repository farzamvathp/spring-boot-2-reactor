package com.emersun.imi.security;

import com.emersun.imi.collections.Permission;
import com.emersun.imi.configs.Constants;
import com.emersun.imi.exceptions.UnauthorizedException;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Component
public class JwtTokenProvider {
    @Autowired
    private Messages messages;
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.access.expire-in-days}")
    private Long accessTokenValidityInDays;
    @Value("${security.jwt.token.refresh.expire-in-days}")
    private Long refreshTokenValidityInDays;
    private final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);


    @Autowired
    private AccountUserDetails accountUserDetails;

    public TokenModel createToken(String username, Optional<String> optionalRefresh, Set<Permission> permissions) {
        TokenModel tokenModel = new TokenModel();
        Long expire = new Date().getTime();
        tokenModel.setAccessTokenExpireTime(new Date(
                LocalDateTime.now(ZoneId.of("Asia/Tehran"))
                        .plusDays(accessTokenValidityInDays)
                        .atZone(ZoneId.of("Asia/Tehran"))
                        .toInstant().toEpochMilli()
        ).getTime());
        tokenModel.setRefreshTokenExpireTime(new Date(
                LocalDateTime.now(ZoneId.of("Asia/Tehran"))
                        .plusDays(refreshTokenValidityInDays)
                        .atZone(ZoneId.of("Asia/Tehran"))
                        .toInstant().toEpochMilli()
        ).getTime());
        tokenModel.setAccessToken(createAccessToken(username));
        Option.ofOptional(optionalRefresh)
                .peek(refresh -> tokenModel.setRefreshToken(refresh))
                .onEmpty(() -> tokenModel.setRefreshToken(createRefreshToken(username)));
        tokenModel.setPermissions(permissions);
        return tokenModel;
    }

    public String createAccessToken(String username) {
        return createToken(username, Constants.ACCESS_TOKEN_AUDIENCE,new Date(
                LocalDateTime.now(ZoneId.of("Asia/Tehran"))
                        .plusDays(accessTokenValidityInDays)
                        .atZone(ZoneId.of("Asia/Tehran"))
                        .toInstant().toEpochMilli()
        ).getTime());
    }

    public String createRefreshToken(String username) {
        return createToken(username,Constants.REFRESH_TOKEN_AUDIENCE, new Date(
                LocalDateTime.now(ZoneId.of("Asia/Tehran"))
                        .plusDays(refreshTokenValidityInDays)
                        .atZone(ZoneId.of("Asia/Tehran"))
                        .toInstant().toEpochMilli()
        ).getTime());
    }

    private String createToken(String username,String audience,Long expire) {
        Claims claims = Jwts.claims()
                .setSubject(username)
                .setAudience(audience);
        Date now = new Date();
        Date validity = new Date(expire);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    public Authentication getAuthentication(String username) {
        UserDetails userDetails = accountUserDetails.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String retrieveToken(HttpServletRequest request) {
        String token = request.getHeader("X-AUTH-TOKEN");
        if(token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token,String audience) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            if(claims.getAudience().equals(audience))
                return true;
            else
                throw new UnauthorizedException(messages.get(Response.ACCESS_DENIED));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Exception : {}",e);
            throw new UnauthorizedException(messages.get(Response.ACCESS_DENIED));
        }
    }
}
