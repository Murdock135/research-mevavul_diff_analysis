class deserialize {
private static Object deserialize(String serialized) throws ClassNotFoundException, IOException {
        byte[] bytes = Base64.decode(serialized);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        try {
            DelegatingSerializationFilter filter = new DelegatingSerializationFilter();
            in = new ObjectInputStream(bis);
            filter.setFilter(in, "javax.security.auth.kerberos.KerberosTicket;javax.security.auth.kerberos.KerberosPrincipal;javax.security.auth.kerberos.KeyImpl;java.net.InetAddress;java.util.Date;!*");
            return in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
