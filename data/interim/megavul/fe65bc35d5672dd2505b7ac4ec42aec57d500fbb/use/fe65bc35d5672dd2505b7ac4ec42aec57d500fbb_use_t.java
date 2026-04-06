class use {
public void use(String resource, Map<String, Object> parameters, XWikiContext context)
    {
        useResource(resource, context);

        // In case a previous call added some parameters, remove them, since the last call for a resource always
        // discards previous ones.
        if (parameters == null) {
            getParametersMap(context).remove(resource);
        } else {
            // Associate parameters to the resource
            getParametersMap(context).put(resource, parameters);
        }

        getSkinExtensionAsync().use(getName(), resource, parameters);
    }
}
