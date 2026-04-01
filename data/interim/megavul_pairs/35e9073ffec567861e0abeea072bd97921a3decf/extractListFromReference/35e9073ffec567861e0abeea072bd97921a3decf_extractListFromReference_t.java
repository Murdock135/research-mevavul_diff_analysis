class extractListFromReference {
private List<String> extractListFromReference(EntityReference reference)
    {
        List<String> path = new ArrayList<>();
        // Add the spaces
        EntityReference spaceReference = reference.extractReference(EntityType.SPACE);
        EntityReference wikiReference = reference.extractReference(EntityType.WIKI);
        for (EntityReference singleReference : spaceReference.removeParent(wikiReference).getReversedReferenceChain()) {
            path.add(singleReference.getName());
        }
        if (reference.getType() == EntityType.DOCUMENT || reference.getType() == EntityType.ATTACHMENT) {
            path.add(reference.getName());
        }
        return path;
    }
}
