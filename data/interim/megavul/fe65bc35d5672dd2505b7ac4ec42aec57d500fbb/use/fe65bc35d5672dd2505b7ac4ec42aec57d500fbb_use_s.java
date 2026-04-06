class use {
public void use(String resource, Map<String, Object> parameters, XWikiContext context)
    {
        useResource(resource, context);

        // Associate parameters to the resource
        getParametersMap(context).put(resource, parameters);

        getSkinExtensionAsync().use(getName(), resource, parameters);
    }
}
