class prepareGadgets {
private List<Gadget> prepareGadgets(List<BaseObject> objects, Syntax sourceSyntax,
        MacroTransformationContext context) throws Exception
    {
        List<Gadget> gadgets = new ArrayList<>();

        // prepare velocity tools to render title
        VelocityContext velocityContext = velocityManager.getVelocityContext();
        // Use the Transformation id as the name passed to the Velocity Engine. This name is used internally
        // by Velocity as a cache index key for caching macros.
        String key = context.getTransformationContext().getId();
        if (key == null) {
            key = "unknown namespace";
        }
        VelocityEngine velocityEngine = velocityManager.getVelocityEngine();

        for (BaseObject xObject : objects) {
            if (xObject != null) {
                this.progress.startStep(this, "dashboard.progress.prepareGadget", "Prepare gadget [{}:{}]",
                    xObject.getDocumentReference(), xObject.getNumber());

                // get the data about the gadget from the object
                // TODO: filter for dashboard name when that field will be in
                String title = xObject.getStringValue("title");
                String content = xObject.getLargeStringValue("content");
                String position = xObject.getStringValue("position");
                String id = xObject.getNumber() + "";

                // render title with velocity
                StringWriter writer = new StringWriter();
                // FIXME: the engine has an issue with $ and # as last character. To test and fix if it happens
                velocityEngine.evaluate(velocityContext, writer, key, title);
                String gadgetTitle = writer.toString();

                // parse both the title and content in the syntax of the transformation context
                List<Block> titleBlocks =
                    renderGadgetProperty(gadgetTitle, sourceSyntax, xObject.getDocumentReference(),
                        xObject.getOwnerDocument(), context);
                List<Block> contentBlocks =
                    renderGadgetProperty(content, sourceSyntax, xObject.getDocumentReference(),
                        xObject.getOwnerDocument(), context);

                // create a gadget will all these and add the gadget to the container of gadgets
                Gadget gadget = new Gadget(id, titleBlocks, contentBlocks, position);
                gadget.setTitleSource(title);
                gadgets.add(gadget);
            } else {
                this.progress.startStep(this, "dashboard.progress.skipNullGadget", "Null gadget object");
            }

            this.progress.endStep(this);
        }

        return gadgets;
    }
}
