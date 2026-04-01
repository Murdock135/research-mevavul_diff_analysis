class parseXML {
public long parseXML(Reader input) throws XMLStreamException {
        Map<String, String> cache = new HashMap<>(1024*32);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty("javax.xml.stream.isCoalescing", true);
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        XMLEventReader reader = inputFactory.createXMLEventReader(input);
        Entity last = null;
        Map<String, Key> nodeKeys = new HashMap<>();
        Map<String, Key> relKeys = new HashMap<>();
        int count = 0;
        try (BatchTransaction tx = new BatchTransaction(db, batchSize * 10, reporter)) {

            while (reader.hasNext()) {
                XMLEvent event;
                try {
                    event = (XMLEvent) reader.next();
                    if ( event.getEventType() == XMLStreamConstants.DTD) {
                        generateXmlDoctypeException();
                    }
                } catch (Exception e) {
                    // in case of unicode invalid chars we skip the event, or we exit in case of EOF
                    if (e.getMessage().contains("Unexpected EOF")) {
                        break;
                    } else if (e.getMessage().contains("DOCTYPE")) {
                        throw e;
                    }
                    continue;
                }
                if (event.isStartElement()) {

                    StartElement element = event.asStartElement();
                    String name = element.getName().getLocalPart();

                    if (name.equals("graphml") || name.equals("graph")) continue;
                    if (name.equals("key")) {
                        String id = getAttribute(element, ID);
                        Key key = new Key(getAttribute(element, NAME), getAttribute(element, TYPE), getAttribute(element, LIST), getAttribute(element, FOR));

                        XMLEvent next = peek(reader);
                        if (next.isStartElement() && next.asStartElement().getName().getLocalPart().equals("default")) {
                            reader.nextEvent().asStartElement();
                            key.setDefault(reader.nextEvent().asCharacters().getData());
                        }
                        if (key.forNode) nodeKeys.put(id, key);
                        else relKeys.put(id, key);
                        continue;
                    }
                    if (name.equals("data")) {
                        if (last == null) continue;
                        String id = getAttribute(element, KEY);
                        boolean isNode = last instanceof Node;
                        Key key = isNode ? nodeKeys.get(id) : relKeys.get(id);
                        if (key == null) key = Key.defaultKey(id, isNode);
                        final Map.Entry<XMLEvent, Object> eventEntry = getDataEventEntry(reader, key);
                        final XMLEvent next = eventEntry.getKey();
                        final Object value = eventEntry.getValue();
                        if (value != null) {
                            if (this.labels && isNode && id.equals("labels")) {
                                addLabels((Node)last,value.toString());
                            } else if (!this.labels || isNode || !id.equals("label")) {
                                last.setProperty(key.nameOrId, value);
                                if (reporter != null) reporter.update(0, 0, 1);
                            }
                        } else if (next.getEventType() == XMLStreamConstants.END_ELEMENT) {
                            last.setProperty(key.nameOrId, StringUtils.EMPTY);
                            reporter.update(0, 0, 1);
                        }
                        continue;
                    }
                    if (name.equals("node")) {
                        tx.increment();
                        String id = getAttribute(element, ID);
                        Node node = tx.getTransaction().createNode();
                        if (this.labels) {
                            String labels = getAttribute(element, LABELS);
                            addLabels(node, labels);
                        }
                        if (storeNodeIds) node.setProperty("id", id);
                        setDefaults(nodeKeys, node);
                        last = node;
                        cache.put(id, node.getElementId());
                        if (reporter != null) reporter.update(1, 0, 0);
                        count++;
                        continue;
                    }
                    if (name.equals("edge")) {
                        tx.increment();
                        String label = getAttribute(element, LABEL);
                        Node from = getByNodeId(cache, tx.getTransaction(), element, XmlNodeExport.NodeType.SOURCE);
                        Node to = getByNodeId(cache, tx.getTransaction(), element, XmlNodeExport.NodeType.TARGET);

                        RelationshipType relationshipType = label == null ? getRelationshipType(reader) : RelationshipType.withName(label);
                        Relationship relationship = from.createRelationshipTo(to, relationshipType);
                        setDefaults(relKeys, relationship);
                        last = relationship;
                        if (reporter != null) reporter.update(0, 1, 0);
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
