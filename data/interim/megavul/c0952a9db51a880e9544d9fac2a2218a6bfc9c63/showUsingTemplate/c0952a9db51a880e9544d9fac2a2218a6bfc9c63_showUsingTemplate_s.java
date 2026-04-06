class showUsingTemplate {
private void showUsingTemplate(
            final VariableHost exporter,
            final String uri,
            final String namespace,
            final boolean includeDoc,
            final Template template,
            final boolean withIndex,
            final PrintWriter out,
            final String... vars
    ) throws IOException {
        final String name = (namespace == null ? "Global" : namespace);

        final Map<String, Object> root = new HashMap<String, Object>();
        final DateFormat df = SimpleDateFormat.getDateTimeInstance();
        root.put("urlPath", uri);
        root.put("name", name);
        root.put("date", df.format(new Date()));
        root.put("includeDoc", includeDoc);

        final List<Variable> varList;
        if (vars != null && vars.length == 1) {
            final Variable v = exporter.getVariable(vars[0]);
            if (v != null) {
                varList = Lists.newArrayListWithExpectedSize(1);
                addVariable(v, varList);
            } else {
                varList = ImmutableList.of();
            }
        } else {
            varList = Lists.newArrayListWithExpectedSize(vars != null ? vars.length : 256);
            if (vars == null || vars.length == 0) {
                exporter.visitVariables(new VariableVisitor() {
                    public void visit(Variable var) {
                        addVariable(var, varList);
                    }
                });
            } else {
                for (String var : vars) {
                    Variable v = exporter.getVariable(var);
                    if (v != null) {
                        addVariable(v, varList);
                    }
                }
            }
        }
        root.put("vars", varList);
        if (withIndex) {
            final String varsIndex = buildIndex(varList);
            root.put("varsIndex", varsIndex);
        }

        try {
            template.process(root, out);
        } catch (Exception e) {
            throw new IOException("template failure", e);
        }
    }
}
