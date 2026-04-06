class resolveEntity {
public InputSource resolveEntity(String publicId, String systemId) throws IOException {

        // lookup the system id caches first
        byte[] content;
        systemId = translateLegacySystemId(systemId);
        content = m_cachePermanent.get(systemId);
        if (content != null) {
            // permanent cache contains system id
            return createInputSource(content, systemId);
        } else if (systemId.equals(CmsXmlPage.XMLPAGE_XSD_SYSTEM_ID)) {

            // XML page XSD reference
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(XMLPAGE_XSD_LOCATION)) {
                content = CmsFileUtil.readFully(stream);
                // cache the XML page DTD
                m_cachePermanent.put(systemId, content);
                return createInputSource(content, systemId);
            } catch (Throwable t) {
                LOG.error(
                    Messages.get().getBundle().key(Messages.LOG_XMLPAGE_XSD_NOT_FOUND_1, XMLPAGE_XSD_LOCATION),
                    t);
            }

        } else if (systemId.equals(XMLPAGE_OLD_DTD_SYSTEM_ID_1) || systemId.endsWith(XMLPAGE_OLD_DTD_SYSTEM_ID_2)) {

            // XML page DTD reference
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(XMLPAGE_OLD_DTD_LOCATION)) {
                // cache the XML page DTD
                content = CmsFileUtil.readFully(stream);
                m_cachePermanent.put(systemId, content);
                return createInputSource(content, systemId);
            } catch (Throwable t) {
                LOG.error(
                    Messages.get().getBundle().key(Messages.LOG_XMLPAGE_DTD_NOT_FOUND_1, XMLPAGE_OLD_DTD_LOCATION),
                    t);
            }
        } else if ((m_cms != null) && systemId.startsWith(OPENCMS_SCHEME)) {

            // opencms:// VFS reference
            String cacheSystemId = systemId.substring(OPENCMS_SCHEME.length() - 1);
            String cacheKey = getCacheKey(
                cacheSystemId,
                m_cms.getRequestContext().getCurrentProject().isOnlineProject());
            // look up temporary cache
            content = m_cacheTemporary.get(cacheKey);
            if (content != null) {
                return createInputSource(content, systemId);
            }
            String storedSiteRoot = m_cms.getRequestContext().getSiteRoot();
            try {
                // content not cached, read from VFS
                m_cms.getRequestContext().setSiteRoot("/");
                CmsFile file = m_cms.readFile(cacheSystemId, CmsResourceFilter.IGNORE_EXPIRATION);
                content = file.getContents();
                // store content in cache
                m_cacheTemporary.put(cacheKey, content);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(Messages.get().getBundle().key(Messages.LOG_ERR_CACHED_SYS_ID_1, cacheKey));
                }
                return createInputSource(content, systemId);
            } catch (CmsException e) {
                throw new IOException(
                    Messages.get().getBundle().key(Messages.LOG_ENTITY_RESOLVE_FAILED_1, systemId),
                    e);
            } finally {
                m_cms.getRequestContext().setSiteRoot(storedSiteRoot);
            }

        } else if (systemId.startsWith(INTERNAL_SCHEME)) {
            String location = systemId.substring(INTERNAL_SCHEME.length());
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(location)) {
                content = CmsFileUtil.readFully(stream);
                m_cachePermanent.put(systemId, content);
                return createInputSource(content, systemId);
            } catch (Throwable t) {
                LOG.error(t.getLocalizedMessage(), t);
            }

        } else if (systemId.substring(0, systemId.lastIndexOf("/") + 1).equalsIgnoreCase(
            CmsConfigurationManager.DEFAULT_DTD_PREFIX)) {
            // default DTD location in the org.opencms.configuration package
            String location = null;
            try {
                String dtdFilename = systemId.substring(systemId.lastIndexOf("/") + 1);
                location = CmsConfigurationManager.DEFAULT_DTD_LOCATION + dtdFilename;
                InputStream stream = getClass().getClassLoader().getResourceAsStream(location);
                content = CmsFileUtil.readFully(stream);
                // cache the DTD
                m_cachePermanent.put(systemId, content);
                return createInputSource(content, systemId);
            } catch (Throwable t) {
                LOG.error(Messages.get().getBundle().key(Messages.LOG_DTD_NOT_FOUND_1, location), t);
            }
        }
        // use the default behaviour (i.e. resolve through external URL)
        return null;
    }
}
