class addResults {
private void addResults(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
    String input = request.getParameter("input");
    if (input == null) {
      return;
    }
    input = input.trim();
    if (input.isEmpty()) {
      return;
    }

    PrintWriter out = response.getWriter();
    if (input.length() > MAXIMUM_QUERY_LENGTH) {
      out.print("This query is too long.  If you want to run very long queries, please download and use our <a href=\"http://nlp.stanford.edu/software/CRF-NER.html\">publicly released distribution</a>.");
      return;
    }

    String outputFormat = request.getParameter("outputFormat");
    if (outputFormat == null || outputFormat.trim().isEmpty()) {
      outputFormat = this.format;
    }

    boolean preserveSpacing;
    String preserveSpacingStr = request.getParameter("preserveSpacing");
    if (preserveSpacingStr == null || preserveSpacingStr.trim().isEmpty()) {
      preserveSpacing = this.spacing;
    } else {
      preserveSpacingStr = preserveSpacingStr.trim();
      preserveSpacing = Boolean.valueOf(preserveSpacingStr);
    }

    String classifier = request.getParameter("classifier");
    if (classifier == null || classifier.trim().isEmpty()) {
      classifier = this.defaultClassifier;
    }

    CRFClassifier<CoreMap> nerModel = ners.get(classifier);
    // check that we weren't asked for a classifier that doesn't exist
    if (nerModel == null) {
      out.print(StringEscapeUtils.escapeHtml4("Unknown model " + classifier));
      return;
    }

    if (outputFormat.equals("highlighted")) {
      outputHighlighting(out, nerModel, input);
    } else {
      out.print(StringEscapeUtils.escapeHtml4(nerModel.classifyToString(input, outputFormat, preserveSpacing)));
    }

    response.addHeader("classifier", classifier);
    // a non-existent outputFormat would have just thrown an exception
    response.addHeader("outputFormat", outputFormat);
    response.addHeader("preserveSpacing", String.valueOf(preserveSpacing));
  }
}
