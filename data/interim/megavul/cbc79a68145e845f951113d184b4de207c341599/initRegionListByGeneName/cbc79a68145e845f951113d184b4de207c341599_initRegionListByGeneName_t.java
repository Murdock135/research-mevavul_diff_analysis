class initRegionListByGeneName {
private static void initRegionListByGeneName(String geneName) throws Exception {
        String sql = "SELECT * FROM gene_region WHERE gene_name=?";

        PreparedStatement stmt = DBManager.prepareStatement(sql);
        stmt.setString(1, geneName);
        ResultSet rset = stmt.executeQuery();

        if (rset.next()) {
            query = rset.getString("gene_name");
            String regionStr = rset.getString("region");

            initRegionListByStr(regionStr);
        }

        rset.close();
    }
}
