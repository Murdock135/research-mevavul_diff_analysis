class extractPrincipalFromWebToken {
protected Object extractPrincipalFromWebToken(Jwt jwt) {
        Map body = (Map) jwt.getBody();
        String base64Principal = (String) body.get("serialized-principal");
        byte[] serializedPrincipal = Base64.decode(base64Principal);
        Object principal;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(codeBase.asClassLoader()); //In case the serialized principal is a POJO entity
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(serializedPrincipal)) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    return codeBase.loadClass(desc.getName());
                }
            };
            principal = objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            throw new AuthenticationException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
        return principal;
    }
}
