class handle_3 {
public void handle(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception
  {
     new ContextualHttpServletRequest(request)
     {
        @Override
        public void process() throws Exception
        {
           ServletContexts.instance().setRequest(request);
           
           if (request.getQueryString() == null)
           {
              throw new ServletException("Invalid request - no component specified");
           }
                     
           Set<Component> components = new HashSet<Component>();
           Set<Type> types = new HashSet<Type>();
           
           response.setContentType("text/javascript");

           Enumeration e = request.getParameterNames();
           while (e.hasMoreElements())
           {
              String componentName = ((String) e.nextElement()).trim();
              
              Component component = Component.forName(componentName);
              if (component == null)
              {                 
                 log.error(String.format("Component not found: [%s]", componentName));
                 throw new ServletException("Invalid request - component not found.");
              }
              else
              {
                 components.add(component);
              }
           }
           
           generateComponentInterface(components, response.getOutputStream(), types);            
       }
     }.run();
  }
}
