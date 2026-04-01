class visit_1 {
@Override
    public void visit(Modification modification) {
        modifiedFilesJson = new ArrayList();

        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("user", modification.getUserDisplayName());
        jsonMap.put("revision", modification.getRevision());
        jsonMap.put("date", formatISO8601(modification.getModifiedTime()));
        String comment = modification.getComment();
        if (!revision.getMaterial().getMaterialType().equals(TYPE)) {
            comment = commentRenderer.render(comment);
        }
        jsonMap.put("comment", comment);
        jsonMap.put("modifiedFiles", modifiedFilesJson);

        modificationsJson.add(jsonMap);
    }
}
