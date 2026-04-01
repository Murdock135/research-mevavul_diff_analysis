class version {
public HgVersion version() {
        CommandLine hg = createCommandLine("hg").withArgs("version").withEncoding("UTF-8");
        String hgOut = execute(hg, new NamedProcessTag("hg version check")).outputAsString();
        return HgVersion.parse(hgOut);
    }
}
