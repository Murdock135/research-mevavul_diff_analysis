class visit {
@Override
    public void visit(MaterialRevision revision) {
        this.revision = revision;
        modificationsJson = new ArrayList<>();

        materialJson = new LinkedHashMap<>();
        materialJson.put("revision", revision.getRevision().getRevision());
        materialJson.put("revision_href", revision.getRevision().getRevisionUrl());
        materialJson.put("user", revision.buildCausedBy());
        materialJson.put("date", formatISO8601(revision.getDateOfLatestModification()));
        materialJson.put("changed", valueOf(revision.isChanged()));
        materialJson.put("modifications", modificationsJson);

        materials.add(materialJson);
    }
}
