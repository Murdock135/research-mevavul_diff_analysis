class doPost_2 {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Checks whether session has timed out
        if (System.currentTimeMillis() > (request.getSession().getLastAccessedTime() + 300000)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("Error");    //New Request Dispatcher
            request.setAttribute("error", "Login session timed out, please click retry to log back in");
            request.setAttribute("previous", "index.html");
            dispatcher.forward(request, response);    //Forwards to the page
        } else {
            HttpSession httpSession = request.getSession();
            Session session = (Session) httpSession.getAttribute("session");
            String user = session.getProperties().getProperty("mail.user");
            String searchQueryForeName = request.getParameter("forename");
            String searchQuerySurName = request.getParameter("surname");

            Model m = new Model(user);

            try {
                String resultTable = m.search(searchQueryForeName, searchQuerySurName, user);
                httpSession.setAttribute("results", resultTable);
                httpSession.setAttribute("success", "");
                request.getRequestDispatcher("contact.jsp").forward(request, response);
            } catch (SQLException e) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("Error"); //New Request Dispatcher
                request.setAttribute("error", e.getMessage());
                request.setAttribute("previous", "searchcontact");
                dispatcher.forward(request, response);
            }
        }
    }
}
