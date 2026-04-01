class addFlags {
public void addFlags(String flagSet) {
        StringBuilder sb = new StringBuilder();
        sb.append(flagSet);
        if (!flagSet.contains("-oX")) {
            sb.append(" -oX -");
        }
        flags.addFlag(sb.toString());
    }
}
