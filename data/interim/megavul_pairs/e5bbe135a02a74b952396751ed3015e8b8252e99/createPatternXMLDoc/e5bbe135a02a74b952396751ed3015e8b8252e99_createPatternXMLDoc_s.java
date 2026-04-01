class createPatternXMLDoc {
private static Document createPatternXMLDoc(List<SsurgeonPattern> patterns) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document domDoc = db.newDocument();
      Element rootElt = domDoc.createElement(SsurgeonPattern.ELT_LIST_TAG);
      domDoc.appendChild(rootElt);
      int ordinal = 1;
      for (SsurgeonPattern pattern : patterns) {
        Element patElt = domDoc.createElement(SsurgeonPattern.SSURGEON_ELEM_TAG);
        patElt.setAttribute(SsurgeonPattern.ORDINAL_ATTR, String.valueOf(ordinal));
        Element semgrexElt = domDoc.createElement(SsurgeonPattern.SEMGREX_ELEM_TAG);
        semgrexElt.appendChild(domDoc.createTextNode(pattern.getSemgrexPattern().pattern()));
        patElt.appendChild(semgrexElt);
        Element uidElem = domDoc.createElement(SsurgeonPattern.UID_ELEM_TAG);
        uidElem.appendChild(domDoc.createTextNode(pattern.getUID()));
        patElt.appendChild(uidElem);
        Element notesElem = domDoc.createElement(SsurgeonPattern.NOTES_ELEM_TAG);
        notesElem.appendChild(domDoc.createTextNode(pattern.getNotes()));
        patElt.appendChild(notesElem);

        SemanticGraph semgrexGraph = pattern.getSemgrexGraph();
        if (semgrexGraph != null) {
          Element patNode = domDoc.createElement(SsurgeonPattern.SEMGREX_GRAPH_ELEM_TAG);
          patNode.appendChild(domDoc.createTextNode(semgrexGraph.toCompactString()));
        }
        Element editList = domDoc.createElement(SsurgeonPattern.EDIT_LIST_ELEM_TAG);
        patElt.appendChild(editList);
        int editOrdinal = 1;
        for (SsurgeonEdit edit : pattern.getEditScript()) {
          Element editElem = domDoc.createElement(SsurgeonPattern.EDIT_ELEM_TAG);
          editElem.setAttribute(SsurgeonPattern.ORDINAL_ATTR, String.valueOf(editOrdinal));
          editElem.appendChild(domDoc.createTextNode(edit.toEditString()));
          editList.appendChild(editElem);
          editOrdinal++;
        }
        rootElt.appendChild(patElt);
        ordinal++;
      }
      return domDoc;
    } catch (Exception e) {
      log.error(Ssurgeon.class.getName(), "createPatternXML");
      log.error(e);
      return null;
    }
  }
}
