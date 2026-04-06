class doPost_1 {
@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
          AccessControlContext acc = AccessController.getContext();
          Subject subject = Subject.getSubject(acc);
          if (subject == null) {
            Helpers.doForbidden(response);
            return;
          }
          session = request.getSession(true);
          session.setAttribute("subject", subject);
        } else {
          Subject subject = (Subject) session.getAttribute("subject");
          if (subject == null) {
            session.invalidate();
            Helpers.doForbidden(response);
            return;
          }
        }

        String encoding = request.getHeader("Accept-Encoding");
        boolean supportsGzip = (encoding != null && encoding.toLowerCase().indexOf("gzip") > -1);
        SessionTerminal st = (SessionTerminal) session.getAttribute("terminal");
        if (st == null || st.isClosed()) {
            st = new SessionTerminal(getCommandProcessor(), getThreadIO());
            session.setAttribute("terminal", st);
        }
        String str = request.getParameter("k");
        String f = request.getParameter("f");
        String dump = st.handle(str, f != null && f.length() > 0);
        if (dump != null) {
            if (supportsGzip) {
                response.setHeader("Content-Encoding", "gzip");
                response.setHeader("Content-Type", "text/html");
                try {
                    GZIPOutputStream gzos = new GZIPOutputStream(response.getOutputStream());
                    gzos.write(dump.getBytes());
                    gzos.close();
                } catch (IOException ie) {
                    LOG.info("Exception writing response: ", ie);
                }
            } else {
                response.getOutputStream().write(dump.getBytes());
            }
        }
    }
}
