class service {
public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        
        Integer sc = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String msg = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Throwable err = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (err != null) {
            err.printStackTrace(pw);
        } else {
            pw.println("(none)");
        }
        pw.flush();
         
        // If we are here there was no error servlet, so show the default error page
        String output = Launcher.RESOURCES.getString("WinstoneResponse.ErrorPage",
                new String[] { sc + "", URIUtil.htmlEscape(msg == null ? "" : msg), URIUtil.htmlEscape(sw.toString()),
                Launcher.RESOURCES.getString("ServerVersion"),
                        "" + new Date() });
        response.setContentLength(output.getBytes(response.getCharacterEncoding()).length);
        Writer out = response.getWriter();
        out.write(output);
        out.flush();
    }
}
