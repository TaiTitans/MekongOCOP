package com.mekongocop.apigateway.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


@Component
public class JwtTokenProvider {

    @Value("656273f802143fc9ce8885452c089da19c96e66043f4d3b87358206c454fe41b5e09c29f63e3d9c88d27dc54ea1b4d9332ef5ef88e338db6b20c01f81c2cdf65bc40bbbcdd1a368b41b945904e957b8dd6f699daa9422bac4db825968390de959939441d6ea84bf6480fde7b36101a5331b6b5eba33edc8059d5e0cd11d1ecc0e92ac14f1af8a30658c9f4ace9583967a3256c905348ad9831d1f00df1c124fd98310f10126997a3b983e6d121112ed5ebe3a608eefcad2d020791fd7eeda2788ae598ed3277d0514277723101540b950bebf8c54ab832a04af295833e03bba1cc0f9419e3a54ff4e8ac56d56ed61125211b37171b711d6262a946ce0e9b2c825b77dca2f74f87c66cc642c99546bc540d26283364264cc3fcfa59ebc505460303d35b1e472f6b73c71933aeb83b797e2ed8f4da5f11dc79beed4fbfe144d12af3169629ae6df125d3fcbea69d364f8048ca9fac1788aa3121ff19e2f75100ea51181792927a168aee0da5cae27afd3d5e152b929efd25bdb400a7082da3c4aab32700ed8e9e785fa8f6763aeacb572bc25ea1d2fae3e8a7ca62d4178bee0fb28fef68c7bb18eb0977ed6a1c0f5b4840c9cb42497c01793553f7e95bc495fe9ae0ff3c03228d0554869fdf1892afd91b82319171bf6ee891ca05e12ce2334caa4823c86fea9f32407b73e8d5b5460a4d4fcc3f949823498015b9cb6589c7c8ae")
    private String secret;

    @Value("${jwt.access-token-expiration-in-ms}")
    private long accessTokenExpirationInMs;

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}