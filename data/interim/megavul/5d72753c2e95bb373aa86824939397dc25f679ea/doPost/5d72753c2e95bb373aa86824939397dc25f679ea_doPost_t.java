class doPost {
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String username = request.getParameter("username"); //Gets all the parameters
        String password = request.getParameter("password");
        Properties prop = System.getProperties();

        try {

            prop.put("mail.user", username);
            prop.put("mail.password", password);
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(prop);
            Store store = session.getStore("imaps");
            store.connect("imap.googlemail.com", username, password);

            request.getSession().setAttribute("session", session);

            RequestDispatcher dispatcher = request.getRequestDispatcher("email.html");
            dispatcher.forward(request, response);

        } catch (MessagingException e) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("Error"); //New Request Dispatcher
            request.setAttribute("error", e.getMessage());
            request.setAttribute("previous", "index.html");
            dispatcher.forward(request, response);
        }
    }
}
