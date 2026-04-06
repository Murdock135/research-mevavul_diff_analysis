class initRegionListByGeneName {
private static void initRegionListByGeneName(String geneName) throws Exception {
        String sql = "SELECT * "
                + "FROM gene_region "
                + "WHERE gene_name='" + geneName + "'";

        ResultSet rset = DBManager.executeQuery(sql);

        if (rset.next()) {
            query = rset.getString("gene_name");
            String regionStr = rset.getString("region");

            initRegionListByStr(regionStr);
        }

        rset.close();
    }
}
