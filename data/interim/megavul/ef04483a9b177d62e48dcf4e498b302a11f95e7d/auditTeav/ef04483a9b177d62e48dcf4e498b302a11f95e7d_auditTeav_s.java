class auditTeav {
private void auditTeav( TrackedEntityAttributeValue av, TrackedEntityAttributeValue createOrUpdateTeav,
        org.hisp.dhis.common.AuditType auditType )
    {
        String currentUsername = currentUserService.getCurrentUsername();

        TrackedEntityAttributeValueAudit deleteTeavAudit = new TrackedEntityAttributeValueAudit( av, av.getAuditValue(),
            currentUsername, DELETE );
        TrackedEntityAttributeValueAudit updatedTeavAudit = new TrackedEntityAttributeValueAudit( createOrUpdateTeav,
            createOrUpdateTeav.getValue(), currentUsername, auditType );

        if ( config.isEnabled( CHANGELOG_TRACKER ) )
        {
            trackedEntityAttributeValueAuditService.addTrackedEntityAttributeValueAudit( deleteTeavAudit );
            trackedEntityAttributeValueAuditService.addTrackedEntityAttributeValueAudit( updatedTeavAudit );
        }
    }
}
