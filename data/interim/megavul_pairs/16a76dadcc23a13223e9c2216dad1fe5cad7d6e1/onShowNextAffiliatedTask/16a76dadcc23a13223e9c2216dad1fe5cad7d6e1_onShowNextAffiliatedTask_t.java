class onShowNextAffiliatedTask {
public void onShowNextAffiliatedTask() {
        // Ensure the device has been provisioned before allowing the user to interact with
        // recents
        if (!isDeviceProvisioned()) {
            return;
        }

        showRelativeAffiliatedTask(true);
    }
}
