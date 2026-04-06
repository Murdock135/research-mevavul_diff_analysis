class validateToken {
public boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
        String signatureToMatch = computeSignature(userDetails, expires);
        return expires >= System.currentTimeMillis() && signature.equals(signatureToMatch);
    }
}
