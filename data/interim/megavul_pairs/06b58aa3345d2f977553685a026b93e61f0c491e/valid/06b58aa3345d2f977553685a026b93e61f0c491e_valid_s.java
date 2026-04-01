class valid {
public boolean valid(String host) {
        return InternetDomainName.isValid(host) || ip(host) || subnet(host);
    }
}
