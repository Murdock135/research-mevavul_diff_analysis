class initRvisByGene {
private static void initRvisByGene(String geneName) throws Exception {
        String sql = "SELECT * FROM rvis WHERE gene_name=?";

        PreparedStatement stmt = DBManager.prepareStatement(sql);
        stmt.setString(1, geneName);
        ResultSet rset = stmt.executeQuery();

        if (rset.next()) {
            float f = FormatManager.getFloat(rset.getObject("rvis_percent"));

            String value = FormatManager.getString(f);

            if (value.equals("-")) {
                Output.rvisPercentile = "NA";
            }

            Output.rvisPercentile = value + "%";
        }

        rset.close();
    }
}
