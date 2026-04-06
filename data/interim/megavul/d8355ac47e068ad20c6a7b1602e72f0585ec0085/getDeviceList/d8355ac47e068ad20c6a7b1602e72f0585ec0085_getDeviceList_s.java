class getDeviceList {
static public CharSequence getDeviceList(ICompanionDeviceManager cdm, LocalBluetoothManager lbm,
            String pkg, int userId) {
        boolean multiple = false;
        StringBuilder sb = new StringBuilder();

        try {
            List<String> associatedMacAddrs = CollectionUtils.mapNotNull(
                    cdm.getAssociations(pkg, userId),
                    a -> a.isSelfManaged() ? null : a.getDeviceMacAddress().toString());
            if (associatedMacAddrs != null) {
                for (String assocMac : associatedMacAddrs) {
                    final Collection<CachedBluetoothDevice> cachedDevices =
                            lbm.getCachedDeviceManager().getCachedDevicesCopy();
                    for (CachedBluetoothDevice cachedBluetoothDevice : cachedDevices) {
                        if (Objects.equals(assocMac, cachedBluetoothDevice.getAddress())) {
                            if (multiple) {
                                sb.append(", ");
                            } else {
                                multiple = true;
                            }
                            sb.append(cachedBluetoothDevice.getName());
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Error calling CDM", e);
        }
        return sb.toString();
    }
}
