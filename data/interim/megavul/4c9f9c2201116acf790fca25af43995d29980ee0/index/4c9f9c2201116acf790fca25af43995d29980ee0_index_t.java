class index {
public void index(TrustAnchor anchor) {
        X500Principal subject;
        X509Certificate cert = anchor.getTrustedCert();
        if (cert != null) {
            subject = cert.getSubjectX500Principal();
        } else {
            subject = anchor.getCA();
        }

        synchronized (subjectToTrustAnchors) {
            List<TrustAnchor> anchors = subjectToTrustAnchors.get(subject);
            if (anchors == null) {
                anchors = new ArrayList<TrustAnchor>(1);
                subjectToTrustAnchors.put(subject, anchors);
            } else {
                // Avoid indexing the same certificate multiple times
                if (cert != null) {
                    for (TrustAnchor entry : anchors) {
                        if (cert.equals(entry.getTrustedCert())) {
                            return;
                        }
                    }
                }
            }
            anchors.add(anchor);
        }
    }
}
