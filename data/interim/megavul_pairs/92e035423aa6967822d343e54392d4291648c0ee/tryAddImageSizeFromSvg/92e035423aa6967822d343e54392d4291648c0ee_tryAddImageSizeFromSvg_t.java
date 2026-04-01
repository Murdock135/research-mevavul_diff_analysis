class tryAddImageSizeFromSvg {
protected List<CmsProperty> tryAddImageSizeFromSvg(byte[] content, List<CmsProperty> properties) {

        if ((content == null) || (content.length == 0)) {
            return properties;
        }
        List<CmsProperty> newProps = properties;
        try {
            double w = -1, h = -1;
            SAXReader reader = new SAXReader();
            reader.setEntityResolver(new CmsXmlEntityResolver(null));
            Document doc = reader.read(new ByteArrayInputStream(content));
            Element node = (Element)(doc.selectSingleNode("/svg"));
            if (node != null) {
                String widthStr = node.attributeValue("width");
                String heightStr = node.attributeValue("height");
                SvgSize width = SvgSize.parse(widthStr);
                SvgSize height = SvgSize.parse(heightStr);
                if ((width != null) && (height != null) && Objects.equals(width.getUnit(), height.getUnit())) {
                    // If width and height are given and have the same units, just interpret them as pixels, otherwise use viewbox
                    w = width.getSize();
                    h = height.getSize();
                } else {
                    String viewboxStr = node.attributeValue("viewBox");
                    if (viewboxStr != null) {
                        viewboxStr = viewboxStr.replace(",", " ");
                        String[] viewboxParts = viewboxStr.trim().split(" +");
                        if (viewboxParts.length == 4) {
                            w = Double.parseDouble(viewboxParts[2]);
                            h = Double.parseDouble(viewboxParts[3]);
                        }
                    }
                }
                if ((w > 0) && (h > 0)) {
                    String propValue = "w:" + (int)Math.round(w) + ",h:" + (int)Math.round(h);
                    Map<String, CmsProperty> propsMap = properties == null
                    ? new HashMap<>()
                    : CmsProperty.toObjectMap(properties);
                    propsMap.put(
                        CmsPropertyDefinition.PROPERTY_IMAGE_SIZE,
                        new CmsProperty(CmsPropertyDefinition.PROPERTY_IMAGE_SIZE, null, propValue));
                    newProps = new ArrayList<>(propsMap.values());
                }

            }
        } catch (Exception e) {
            LOG.error("Error while trying to determine size of SVG: " + e.getLocalizedMessage(), e);
        }
        return newProps;
    }
}
