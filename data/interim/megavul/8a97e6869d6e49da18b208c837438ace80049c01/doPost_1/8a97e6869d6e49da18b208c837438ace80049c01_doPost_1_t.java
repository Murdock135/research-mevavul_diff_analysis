class doPost_1 {
@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userID = request.getParameter("userID");
        String newID = request.getParameter("newID");

        if (newID != null && newID.matches(".*[&<>\"`']+.*")) {
            throw new ServletException("User ID must not contain any HTML markup.");
        }

        // now save to the xml file
        try {
            UserManager userFactory = UserFactory.getInstance();
            userFactory.renameUser(userID, newID);
        } catch (Throwable e) {
            throw new ServletException("Error renaming user " + userID + " to " + newID, e);
        }

        response.sendRedirect("list.jsp");
    }
}
