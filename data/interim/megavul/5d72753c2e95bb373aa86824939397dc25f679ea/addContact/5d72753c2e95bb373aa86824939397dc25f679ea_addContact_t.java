class addContact {
public void addContact(String firstname, String surname, String email, String user) throws SQLException {

        PreparedStatement checkDuplicate = conn.prepareStatement("SELECT * FROM contactinfo WHERE emailaddress = ?");
        checkDuplicate.setString(1, email);
        ResultSet rs = checkDuplicate.executeQuery();
        if (rs.next()) {
            throw new SQLException("Contact already exists");
        }
        PreparedStatement newStudent = conn.prepareStatement("INSERT INTO " +
                "contactinfo (forename, familyname, emailaddress, contactemailaddress) VALUES (?, ?, ?, ?)");
        newStudent.setString(1, firstname);
        newStudent.setString(2, surname);
        newStudent.setString(3, email);
        newStudent.setString(4, user);
        newStudent.execute();

        conn.close();
    }
}
