class addContact {
public void addContact(String firstname, String surname, String email, String user) throws SQLException {
        PreparedStatement newStudent = conn.prepareStatement("INSERT INTO " +
                "contactinfo (forename, familyname, emailaddress, contactemailaddress) VALUES ('" + firstname + "', '" + surname + "', '" + email + "', '" + user + "')");
        newStudent.execute();
    }
}
