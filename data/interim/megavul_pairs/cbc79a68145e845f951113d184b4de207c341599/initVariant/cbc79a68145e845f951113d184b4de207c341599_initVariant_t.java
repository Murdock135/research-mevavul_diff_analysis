class initVariant {
public static void initVariant() throws Exception {
        String[] tmp = Input.idStr.split("-");

        String sql = "SELECT * "
                + "FROM variant_v2 "
                + "WHERE chr= ? AND pos= ? AND ref= ? AND allele= ?";

        PreparedStatement stmt = DBManager.prepareStatement(sql);
        stmt.setString(1, tmp[0]);
        stmt.setInt(2, Integer.valueOf(tmp[1]));
        stmt.setString(3, tmp[2]);
        stmt.setString(4, tmp[3]);
        ResultSet rset = stmt.executeQuery();

        if (rset.next()) {
            variant = new Variant(rset);
        }

        if (variant != null) {
            variant.initAnnotationMap();
        }
    }
}
