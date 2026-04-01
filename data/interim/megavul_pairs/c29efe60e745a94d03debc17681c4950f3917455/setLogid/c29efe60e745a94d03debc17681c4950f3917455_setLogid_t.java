class setLogid {
public void setLogid(String logid) throws PresentationException {
        synchronized (this) {
            if ("-".equals(logid)) {
                this.logid = "";
            } else if(StringUtils.isNotBlank(logid) && logid.matches("[\\w-]+")) {
                this.logid = SolrTools.escapeSpecialCharacters(logid);
            } else {
                throw new PresentationException("The passed logId " + SolrTools.escapeSpecialCharacters(logid) + " contains illegal characters");
            }
        }
    }
}
