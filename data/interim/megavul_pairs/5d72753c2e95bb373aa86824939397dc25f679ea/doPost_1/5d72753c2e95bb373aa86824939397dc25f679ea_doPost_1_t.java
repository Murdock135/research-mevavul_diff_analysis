class doPost_1 {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();    //Gets the PrintWriter
        String back = (String) request.getAttribute("previous");
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
