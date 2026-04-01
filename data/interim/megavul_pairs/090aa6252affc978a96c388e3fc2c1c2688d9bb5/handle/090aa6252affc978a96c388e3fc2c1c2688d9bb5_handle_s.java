class handle {
public void handle(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    // We're sending an XML response, so set the response content type to text/xml
    response.setContentType("text/xml");

    // Parse the incoming request as XML
    SAXReader xmlReader = new SAXReader();
    Document doc = xmlReader.read(request.getInputStream());
    Element env = doc.getRootElement();

    Element body = env.element("body");

    // First handle any new subscriptions
    List<SubscriptionRequest> requests = new ArrayList<SubscriptionRequest>();

    List<Element> elements = body.elements("subscribe");
    for (Element e : elements)
    {
      requests.add(new SubscriptionRequest(e.attributeValue("topic")));
    }

    ServletLifecycle.beginRequest(request);
    try
    {
      ServletContexts.instance().setRequest(request);

      Manager.instance().initializeTemporaryConversation();
      ServletLifecycle.resumeConversation(request);

      for (SubscriptionRequest req : requests)
      {
        req.subscribe();
      }

      // Then handle any unsubscriptions
      List<String> unsubscribeTokens = new ArrayList<String>();

      elements = body.elements("unsubscribe");
      for (Element e : elements) 
      {
        unsubscribeTokens.add(e.attributeValue("token"));
      }

      for (String token : unsubscribeTokens) 
      {
        RemoteSubscriber subscriber = SubscriptionRegistry.instance().
                                      getSubscription(token);
        if (subscriber != null)
        {
          subscriber.unsubscribe();
        }
      }
    }
    finally
    {
      Lifecycle.endRequest();
    }

    // Package up the response
    marshalResponse(requests, response.getOutputStream());
  }
}
