class doPost_1 {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (System.currentTimeMillis() > (request.getSession().getLastAccessedTime() + 300000)) {
            request.setAttribute("error", "Login session timed out, please click retry to log back in");
            request.setAttribute("previous", "index.html");
        }
        PrintWriter out = response.getWriter();    //Gets the PrintWriter
        String back;
        String previous = (String) request.getAttribute("previous");
        if (previous.equals("/LoginController") || previous.equals("index.html")) {
            back = "index.html";
        } else if (previous.equals("searchcontact")) {
            back = "contact.jsp";
        } else {
            back = "email.html";
        }
        out.println(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head lang=\"en\">" +
                        "<meta charset=\"UTF-8\">" +
                        "<title>Error Occured</title>" +
                        "</head>" +
                        "<body>" +
                        "<center>" +
                        "<h1>Error Occurred!</h1>" +
                        "<div>" +
                        "<br>" +
                        "Error: " + request.getAttribute("error") + "<br>" + "<br>" + "<br>" +//Gets the error message
                        "</div>" +
                        "<div class='error-actions'>" +
                        "<a href='" + back + "'>Retry</a>" +
                        "</div>" +
                        "</center>" +
                        "</body>" +
                        "</html>"
        );

    }
}
