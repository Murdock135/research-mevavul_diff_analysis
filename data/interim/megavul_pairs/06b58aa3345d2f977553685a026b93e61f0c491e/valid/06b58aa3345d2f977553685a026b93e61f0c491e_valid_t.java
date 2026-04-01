class valid {
public boolean valid(String host) {
        return InternetDomainName.isValid(host) || isIp(host) || isSubnet(host) || isFile(host);
    }
}
