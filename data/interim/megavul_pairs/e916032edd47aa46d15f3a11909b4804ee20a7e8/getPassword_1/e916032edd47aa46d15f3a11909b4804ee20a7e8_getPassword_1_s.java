class getPassword_1 {
public static String getPassword() {
        return System.getProperty(KieServerConstants.CFG_KIE_PASSWORD, "kieserver1!");
    }
}
