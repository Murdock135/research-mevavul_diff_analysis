class sendError {
public void sendError(int sc, String msg) throws IOException {
        if (isIncluding()) {
            Logger.log(Logger.ERROR, Launcher.RESOURCES, "IncludeResponse.Error",
                    new String[] { "" + sc, msg });
            return;
        }
        
        Logger.log(Logger.DEBUG, Launcher.RESOURCES,
                "WinstoneResponse.SendingError", new String[] { "" + sc, msg });

        if ((this.webAppConfig != null) && (this.req != null)) {
            
            RequestDispatcher rd = this.webAppConfig
                    .getErrorDispatcherByCode(req.getRequestURI(), sc, msg, null);
            if (rd != null) {
                try {
                    rd.forward(this.req, this);
                    return;
                } catch (IllegalStateException err) {
                    throw err;
                } catch (IOException err) {
                    throw err;
                } catch (Throwable err) {
                    Logger.log(Logger.WARNING, Launcher.RESOURCES,
                            "WinstoneResponse.ErrorInErrorPage", new String[] {
                                    rd.getName(), sc + "" }, err);
                    return;
                }
            }
        }
        // If we are here there was no webapp and/or no request object, so 
        // show the default error page
        if (this.errorStatusCode == null) {
            this.statusCode = sc;
        }
        String output = Launcher.RESOURCES.getString("WinstoneResponse.ErrorPage",
                new String[] { sc + "", URIUtil.htmlEscape(msg == null ? "" : msg), "",
                        Launcher.RESOURCES.getString("ServerVersion"),
                        "" + new Date() });
        setContentLength(output.getBytes(getCharacterEncoding()).length);
        Writer out = getWriter();
        out.write(output);
        out.flush();
    }
}
