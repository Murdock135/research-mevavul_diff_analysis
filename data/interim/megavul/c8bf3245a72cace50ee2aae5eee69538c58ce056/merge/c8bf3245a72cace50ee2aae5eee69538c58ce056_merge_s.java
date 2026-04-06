class merge {
public XMLValidationSettings merge(XMLValidationSettings settings) {
		if(settings != null) {
			this.schema = settings.schema;
			this.enabled = settings.enabled;
		}
		return this;
	}
}
