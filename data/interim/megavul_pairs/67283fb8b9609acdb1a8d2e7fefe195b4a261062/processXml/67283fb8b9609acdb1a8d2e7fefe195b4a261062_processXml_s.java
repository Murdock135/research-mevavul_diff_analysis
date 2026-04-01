class processXml {
public boolean processXml() throws WebdavException {
    if (!isAppXml()) {
      return false;
    }

    try {
      reqRdr = req.getReader();
    } catch (final Throwable t) {
      throw new WebdavException(t);
    }

    xmlDoc = parseXml(reqRdr);
    getTheReader = false;
    return true;
  }
}
