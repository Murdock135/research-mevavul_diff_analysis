class handle_1 {
public void handle(HttpServletRequest request, final HttpServletResponse response)
      throws Exception
  {
    // We're sending an XML response, so set the response content type to text/xml
    response.setContentType("text/xml");

    // Parse the incoming request as XML
    SAXReader xmlReader = new SAXReader();
    Document doc = xmlReader.read(request.getInputStream());
    Element env = doc.getRootElement();

    final List<PollRequest> polls = unmarshalRequests(env);

    new ContextualHttpServletRequest(request)
    {
       @Override
       public void process() throws Exception
       {        
          for (PollRequest req : polls)
          {
             req.poll();
          }
      
          // Package up the response
          marshalResponse(polls, response.getOutputStream());      
       }
    }.run();
  }
}
