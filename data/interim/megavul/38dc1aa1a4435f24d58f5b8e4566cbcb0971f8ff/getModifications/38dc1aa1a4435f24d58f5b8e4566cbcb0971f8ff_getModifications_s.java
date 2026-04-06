class getModifications {
@Override
    public History getModifications(String wikiName, Integer start, Integer number, String order, Long ts,
            Boolean withPrettyNames) throws XWikiRestException
    {
        try {
            History history = new History();

            String query = String.format("select doc.space, doc.name, doc.language, rcs.id, rcs.date, rcs.author,"
                + " rcs.comment from XWikiRCSNodeInfo as rcs, XWikiDocument as doc where rcs.id.docId = doc.id and"
                + " rcs.date > :date order by rcs.date %s, rcs.id.version1 %s, rcs.id.version2 %s",
                    order, order, order);

            List<Object> queryResult = null;
            queryResult = queryManager.createQuery(query, Query.XWQL).bindValue("date", new Date(ts)).setLimit(number)
                    .setOffset(start).setWiki(wikiName).execute();

            for (Object object : queryResult) {
                Object[] fields = (Object[]) object;

                String spaceId = (String) fields[0];
                List<String> spaces = Utils.getSpacesFromSpaceId(spaceId);
                String pageName = (String) fields[1];
                String language = (String) fields[2];
                if (language.equals("")) {
                    language = null;
                }
                XWikiRCSNodeId nodeId = (XWikiRCSNodeId) fields[3];
                Timestamp timestamp = (Timestamp) fields[4];
                Date modified = new Date(timestamp.getTime());
                String modifier = (String) fields[5];
                String comment = (String) fields[6];

                HistorySummary historySummary = DomainObjectFactory.createHistorySummary(objectFactory,
                        uriInfo.getBaseUri(), wikiName, spaces, pageName, language, nodeId.getVersion(), modifier,
                        modified, comment, Utils.getXWikiApi(componentManager), withPrettyNames);

                history.getHistorySummaries().add(historySummary);
            }

            return history;
        } catch (QueryException e) {
            throw new XWikiRestException(e);
        }
    }
}
