class decideCancelProvisioningDialog {
@Override
    protected void decideCancelProvisioningDialog() {
        if (getUtils().isDeviceOwnerAction(mParams.provisioningAction)
                || getUtils().isOrganizationOwnedAllowed(mParams)) {
            showCancelProvisioningDialog(/* resetRequired= */ true);
        } else {
            showCancelProvisioningDialog(/* resetRequired= */ false);
        }
    }
}
