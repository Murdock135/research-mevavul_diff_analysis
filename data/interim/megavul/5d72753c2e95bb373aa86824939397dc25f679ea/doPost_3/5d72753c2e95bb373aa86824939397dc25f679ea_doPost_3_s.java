class doPost_3 {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Checks whether session has timed out
        if (System.currentTimeMillis() > (request.getSession().getLastAccessedTime() + 300000)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("Error");    //New Request Dispatcher
            request.setAttribute("error", "Login session timed out, please click retry to log back in");
            request.setAttribute("previous", "index.html");
            dispatcher.forward(request, response);    //Forwards to the page
        } else {

            HttpSession httpSession = request.getSession();     //Get Session details
            Session session = (Session) httpSession.getAttribute("session");
            String user = session.getProperties().getProperty("mail.user");

            String forename = request.getParameter("firstname");
            String surname = request.getParameter("secondname");
            String email = request.getParameter("email");

            Model m = new Model(user);      //Initialize model

            try {
                m.addContact(forename, surname, email, user);
                httpSession.setAttribute("success", "<p id=\"success\">Contact saved successfully</p>");
                request.getRequestDispatcher("addcontact.jsp").forward(request, response);
            } catch (SQLException e) {

                RequestDispatcher dispatcher = request.getRequestDispatcher("Error"); //New Request Dispatcher
                request.setAttribute("error", e.getMessage());
                request.setAttribute("previous", "searchcontact");
                dispatcher.forward(request, response);
            }
        }
    }
}
