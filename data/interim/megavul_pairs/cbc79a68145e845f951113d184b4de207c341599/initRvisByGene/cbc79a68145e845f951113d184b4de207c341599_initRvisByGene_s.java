class initRvisByGene {
private static void initRvisByGene(String geneName) throws Exception {
        String sql = "SELECT * "
                + "FROM rvis "
                + "WHERE gene_name='" + geneName + "'";

        ResultSet rset = DBManager.executeQuery(sql);

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
