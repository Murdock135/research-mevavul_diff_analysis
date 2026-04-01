class internalPrintln {
@SuppressWarnings("unchecked")
    private void internalPrintln(Map<String, Object> options, Object object) {
        if (object == null) {
            return;
        }
        long start = new Date().getTime();
        if (options.containsKey(Printer.EXCLUDE)) {
            List<String> colOut = optionList(Printer.EXCLUDE, options);
            List<String> colIn = optionList(Printer.COLUMNS_IN, options);
            colIn.removeAll(colOut);
            colOut.addAll((List<String>) options.get(Printer.COLUMNS_OUT));
            options.put(Printer.COLUMNS_IN, colIn);
            options.put(Printer.COLUMNS_OUT, colOut);
        }
        if (options.containsKey(Printer.INCLUDE)) {
            List<String> colIn = optionList(Printer.INCLUDE, options);
            colIn.addAll((List<String>) options.get(Printer.COLUMNS_IN));
            options.put(Printer.COLUMNS_IN, colIn);
        }
        options.put(Printer.VALUE_STYLE, valueHighlighter((String) options.getOrDefault(Printer.VALUE_STYLE, null)));
        prntStyle = Styles.prntStyle();
        options.putIfAbsent(Printer.WIDTH, terminal().getSize().getColumns());
        String style = (String) options.getOrDefault(Printer.STYLE, "");
        options.put(Printer.STYLE, valueHighlighter(style));
        int width = (int) options.get(Printer.WIDTH);
        int maxrows = (int) options.get(Printer.MAXROWS);
        if (!style.isEmpty() && object instanceof String) {
            highlightAndPrint(width, (SyntaxHighlighter) options.get(Printer.STYLE), (String) object, true, maxrows);
        } else if (style.equalsIgnoreCase("JSON")) {
            if (engine == null) {
                throw new IllegalArgumentException("JSON style not supported!");
            }
            String json = engine.toJson(object);
            highlightAndPrint(width, (SyntaxHighlighter) options.get(Printer.STYLE), json, true, maxrows);
        } else if (options.containsKey(Printer.SKIP_DEFAULT_OPTIONS)) {
            highlightAndPrint(options, object);
        } else if (object instanceof Exception) {
            highlightAndPrint(options, (Exception) object);
        } else if (object instanceof CmdDesc) {
            highlight((CmdDesc) object).println(terminal());
        } else if (object instanceof String || object instanceof Number) {
            String str = object.toString();
            SyntaxHighlighter highlighter = (SyntaxHighlighter) options.getOrDefault(Printer.VALUE_STYLE, null);
            highlightAndPrint(width, highlighter, str, doValueHighlight(options, str), maxrows);
        } else {
            highlightAndPrint(options, object);
        }
        terminal().flush();
        Log.debug("println: ", new Date().getTime() - start, " msec");
    }
}
