class getCertificateRepositoryObjectValidationContext {
private Stream<Tuple2<CertificateRepositoryObjectValidationContext, ValidationResult>>
        getCertificateRepositoryObjectValidationContext(TrustAnchor trustAnchor,
                                                        CertificateRepositoryObjectValidationContext context,
                                                        List<Key> validatedObjects,
                                                        URI crlUri,
                                                        X509Crl crl,
                                                        Tuple3<URI, RpkiObject, ValidationResult> e) {
        final URI location = e.v1();
        final RpkiObject rpkiObject = e.v2();
        final ValidationResult temporary = e.v3();

        final Optional<CertificateRepositoryObject> maybeCertificateRepositoryObject = Bench.mark(trustAnchor.getName(),
            "rpkiObject.get", () -> rpkiObject.get(CertificateRepositoryObject.class, temporary));

        if (!temporary.hasFailureForCurrentLocation()) {
            if (maybeCertificateRepositoryObject.isPresent()) {
                CertificateRepositoryObject certificateRepositoryObject = maybeCertificateRepositoryObject.get();
                Bench.mark0(trustAnchor.getName(), "certificateRepositoryObject.validate", () ->
                    certificateRepositoryObject.validate(location.toASCIIString(), context, crl, crlUri, validationConfig.validationOptions(), temporary));

                if (!temporary.hasFailureForCurrentLocation()) {
                    validatedObjects.add(rpkiObject.key());
                }

                if (certificateRepositoryObject instanceof X509ResourceCertificate
                    && ((X509ResourceCertificate) certificateRepositoryObject).isCa()
                    && !temporary.hasFailureForCurrentLocation()) {

                    final CertificateRepositoryObjectValidationContext childContext = context.createChildContext(location, (X509ResourceCertificate) certificateRepositoryObject);
                    return Stream.of(new Tuple2<>(childContext, temporary));
                }
            }
        }
        if(validationConfig.isStrictValidation()){
            throw new StrictValidationException("Can't get certificat context");
        } else {
            return Stream.empty();
        }
    }
}
