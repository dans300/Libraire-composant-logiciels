package com.project.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.function.Function;


public class JwtUtils {

    private static final String SECRET_KEY = "secretKeyECommerceProjectINSACVLSPRINGBOOTINSACVLSPRINGBOOT";

    public static String hashPassword(String password) {
        try {
            // Create a SHA-256 hash instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convert the password string to bytes using UTF-8 encoding
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

            // Generate the hash value of the password bytes
            byte[] hashBytes = digest.digest(passwordBytes);

            // Convert the hash bytes to a hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Return the hashed password as a hex string
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }

        return null; // Return null if hashing failed
    }

    public static String extractRole(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Integer extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return (Integer) claims.get("user_id");
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static String generateToken(String role, Long id) {
        return createToken(role,id);
    }

    private static String createToken(String role, Long id) {

        return
                Jwts.builder().
                        setSubject(role).
                        claim("user_id", id)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public static Boolean validateToken(String token, String role) {
        try{
            final String extractedRole = extractRole(token);
            return (extractedRole.equals(role) && !isTokenExpired(token));
        }catch (Exception e){
            return false;
        }
    }
}