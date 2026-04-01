class getPrincipal {
protected Map<String, Serializable> getPrincipal(Jws<Claims> jwt) {
        Map<String, Serializable> principal = new HashMap<>();
        principal.put("jwt", (Serializable) jwt.getBody());
        return principal;
    }
}
