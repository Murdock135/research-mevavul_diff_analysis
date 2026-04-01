class onShowPrevAffiliatedTask {
public void onShowPrevAffiliatedTask() {
        // Ensure the device has been provisioned before allowing the user to interact with
        // recents
        if (!isDeviceProvisioned()) {
            return;
        }

        showRelativeAffiliatedTask(false);
    }
}
