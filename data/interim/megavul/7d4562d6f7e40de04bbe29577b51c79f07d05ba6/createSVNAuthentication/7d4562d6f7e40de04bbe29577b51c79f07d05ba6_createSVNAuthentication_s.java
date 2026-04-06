class createSVNAuthentication {
@Override
            public SVNAuthentication createSVNAuthentication(String kind) {
                if(kind.equals(ISVNAuthenticationManager.SSL))
                    try {
                        SVNSSLAuthentication authentication = new SVNSSLAuthentication(
                                Base64.decode(certificate.getPlainText().toCharArray()),
                                Scrambler.descramble(password), false);
                        authentication.setCertificatePath("dummy"); // TODO: remove this JENKINS-19175 workaround
                        return authentication;
                    } catch (IOException e) {
                        throw new Error(e); // can't happen
                    }
                else
                    return null; // unexpected authentication type
            }
}
