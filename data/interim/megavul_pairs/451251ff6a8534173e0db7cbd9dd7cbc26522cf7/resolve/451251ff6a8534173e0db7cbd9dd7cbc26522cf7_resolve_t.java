class resolve {
@Override
	public String resolve(IFile file, String baseLocation, String publicId, String systemId) {
		if (null != systemId) {
			try {
				URI proposalByPreviousResolver = org.eclipse.emf.common.util.URI.createURI(systemId);
				String host = proposalByPreviousResolver.host();
				/*
				 * The host is empty (not null)
				 */
				if (!(null == host || host.isEmpty())) {
					return REFUSE_EXTERNAL_URI;
				}
			} catch (IllegalArgumentException ignore) {
				//If it is no a valid URI, there is nothing to do here.
			}
		}
		return null; //Don't alter the proposal of previous resolver extensions by proposing something else
	}
}
