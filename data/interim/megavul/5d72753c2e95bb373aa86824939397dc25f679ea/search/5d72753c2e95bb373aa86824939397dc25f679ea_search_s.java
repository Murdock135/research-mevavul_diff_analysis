class search {
public String search(String forename, String surname, String contactemail) throws SQLException {

        String query;
        if (forename.isEmpty() && surname.isEmpty()) {
            query = "";
        } else if(forename.isEmpty()) {
            query = "familyname LIKE '%" + surname + "' and";
        } else if(surname.isEmpty()) {
            query = "forename LIKE '%" + forename + "' and ";
        } else {
            query = "forename LIKE '%" + forename + "' and familyname LIKE '%" + surname + "' and";
        }

    PreparedStatement ps = conn.prepareStatement("SELECT * FROM contactinfo WHERE " + query + " contactemailaddress = '" + contactemail + "'");
    ResultSet rs = ps.executeQuery();
    StringBuilder result = new StringBuilder("<h3>Search results...</h3><table class=\"result-table\">" +
            "<tr>" +
            "<th>Forename</th> <th>Surname</th> <th>Email</th>" +
            "</tr>");
    while(rs.next())

    {
        result.append("<tr><td>");
        result.append(rs.getString(2));
        result.append("</td><td>" + rs.getString(3));
        result.append("</td><td>" + rs.getString(4) + "</td></tr>");
    }

    result.append("</table");
    return result.toString();
}
}
