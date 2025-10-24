package com.app.mindbody.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // The secret key used to sign and verify JWT tokens (loaded from application.properties)
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    // Extracts the username (subject) from a given JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generates a new JWT token for a user (without extra claims)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a new JWT token for a user (with optional extra claims like roles, etc.)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder() // Start building the token
                .setClaims(extraClaims) // Add custom claims (if any)
                .setSubject(userDetails.getUsername()) // The main identity (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // When the token was created
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Expiration time (24 hours here)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign using your secret key + algorithm
                .compact(); // Build the final token string
    }

    // Validates if the token belongs to the user and is not expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Check that usernames match and token is not expired
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Checks if the token has expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extracts the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extracts a specific claim from the token (e.g., username, expiration, etc.)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Get all claims first
        return claimsResolver.apply(claims); // Then return only the one we asked for
    }

    // Parses the JWT token and returns all the claims (payload data)
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // Create a JWT parser
                .setSigningKey(getSignInKey()) // Use your secret key to verify the token
                .build() // Build the parser
                .parseClaimsJws(token) // Parse and validate the token
                .getBody(); // Return the payload (claims)
    }

    // Converts your secret key from Base64 into a Key object used to sign and verify tokens
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode from Base64 to bytes
        return Keys.hmacShaKeyFor(keyBytes); // Create the HMAC-SHA key
    }
}
