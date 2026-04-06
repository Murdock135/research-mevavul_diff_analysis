class use_1 {
public void use(String resource, XWikiContext context)
    {
        useResource(resource, context);

        // In case a previous call added some parameters, remove them, since the last call for a resource always
        // discards previous ones.
        getParametersMap(context).remove(resource);

        // Register the use of the resource in case the current execution is an asynchronous renderer
        getSkinExtensionAsync().use(getName(), resource, null);
    }
}
