class includeHosts {
public void includeHosts(String hosts) {
        if (!validator.valid(hosts)) {
            throw new RuntimeException("Non legal hosts parameter");
        }
        flags.addIncludedHost(hosts);
    }
}
