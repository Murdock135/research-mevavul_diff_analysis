class merge {
public XMLValidationSettings merge(XMLValidationSettings settings) {
		if (settings != null) {
			this.schema = settings.schema;
			this.enabled = settings.enabled;
			this.disallowDocTypeDecl = settings.disallowDocTypeDecl;
			this.resolveExternalEntities = settings.resolveExternalEntities;
		}
		return this;
	}
}
