class initAnnotationMap {
public void initAnnotationMap() throws Exception {
        String sql = "SELECT * "
                + "FROM annotation_v2 "
                + "WHERE variant_id = ? "
                + "ORDER BY igm_rank,"
                // when igm_rank is the same, the data sort by "Canonical" = "YES"
                + "case when canonical is null then 1 else 0 end,canonical;";

        PreparedStatement stmt = DBManager.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rset = stmt.executeQuery();

        while (rset.next()) {
            Annotation anno = new Annotation(rset);

            if (annotation == null) {
                annotation = anno; // the most damaging one
            }

            if (!geneAnnotationMap.containsKey(anno.getGeneName())) {
                geneAnnotationMap.put(anno.getGeneName(), new ArrayList<Annotation>());
            }

            geneAnnotationMap.get(anno.getGeneName()).add(anno);
        }

        rset.close();
    }
}
