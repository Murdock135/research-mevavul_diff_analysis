class processMessage {
@Override
        public boolean processMessage(Message msg) {

            boolean isTurningOn= isTurningOn();
            boolean isTurningOff = isTurningOff();
            boolean isBleTurningOn = isBleTurningOn();
            boolean isBleTurningOff = isBleTurningOff();

            AdapterService adapterService = mAdapterService;
            AdapterProperties adapterProperties = mAdapterProperties;
            if ((adapterService == null) || (adapterProperties == null)) {
                errorLog("Received message in PendingCommandState after cleanup: " + msg.what);
                return false;
            }

            debugLog("Current state: PENDING_COMMAND, message: " + msg.what);

            switch (msg.what) {
                case USER_TURN_ON:
                    if (isBleTurningOff || isTurningOff) { //TODO:do we need to send it after ble turn off also??
                        infoLog("Deferring USER_TURN_ON request...");
                        deferMessage(msg);
                    }
                    break;

                case USER_TURN_OFF:
                    if (isTurningOn || isBleTurningOn) {
                        infoLog("Deferring USER_TURN_OFF request...");
                        deferMessage(msg);
                    }
                    break;

                case BLE_TURN_ON:
                    if (isTurningOff || isBleTurningOff) {
                        infoLog("Deferring BLE_TURN_ON request...");
                        deferMessage(msg);
                    }
                    break;

                case BLE_TURN_OFF:
                    if (isTurningOn || isBleTurningOn) {
                        infoLog("Deferring BLE_TURN_OFF request...");
                        deferMessage(msg);
                    }
                    break;

                case BLE_STARTED:
                    //Remove start timeout
                    removeMessages(BLE_START_TIMEOUT);

                    //Enable
                    boolean isGuest = UserManager.get(mAdapterService).isGuestUser();
                    if (!adapterService.enableNative(isGuest)) {
                        errorLog("Error while turning Bluetooth on");
                        notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                        transitionTo(mOffState);
                    } else {
                        sendMessageDelayed(ENABLE_TIMEOUT, ENABLE_TIMEOUT_DELAY);
                    }
                    break;

                case BREDR_STARTED:
                    //Remove start timeout
                    removeMessages(BREDR_START_TIMEOUT);
                    adapterProperties.onBluetoothReady();
                    mPendingCommandState.setTurningOn(false);
                    transitionTo(mOnState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_ON);
                    break;

                case ENABLED_READY:
                    removeMessages(ENABLE_TIMEOUT);
                    mPendingCommandState.setBleTurningOn(false);
                    transitionTo(mBleOnState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_BLE_ON);
                    break;

                case SET_SCAN_MODE_TIMEOUT:
                     warningLog("Timeout while setting scan mode. Continuing with disable...");
                     //Fall through
                case BEGIN_DISABLE:
                    removeMessages(SET_SCAN_MODE_TIMEOUT);
                    sendMessageDelayed(BREDR_STOP_TIMEOUT, BREDR_STOP_TIMEOUT_DELAY);
                    adapterService.stopProfileServices();
                    break;

                case DISABLED:
                    if (isTurningOn) {
                        removeMessages(ENABLE_TIMEOUT);
                        errorLog("Error enabling Bluetooth - hardware init failed?");
                        mPendingCommandState.setTurningOn(false);
                        transitionTo(mOffState);
                        adapterService.stopProfileServices();
                        notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                        break;
                    }
                    removeMessages(DISABLE_TIMEOUT);
                    sendMessageDelayed(BLE_STOP_TIMEOUT, BLE_STOP_TIMEOUT_DELAY);
                    if (adapterService.stopGattProfileService()) {
                        debugLog("Stopping Gatt profile services that were post enabled");
                        break;
                    }
                    //Fall through if no services or services already stopped
                case BLE_STOPPED:
                    removeMessages(BLE_STOP_TIMEOUT);
                    setBleTurningOff(false);
                    transitionTo(mOffState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                    break;

                case BREDR_STOPPED:
                    removeMessages(BREDR_STOP_TIMEOUT);
                    setTurningOff(false);
                    transitionTo(mBleOnState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_BLE_ON);
                    break;

                case BLE_START_TIMEOUT:
                    errorLog("Error enabling Bluetooth (BLE start timeout)");
                    mPendingCommandState.setBleTurningOn(false);
                    transitionTo(mOffState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                    break;

                case BREDR_START_TIMEOUT:
                    errorLog("Error enabling Bluetooth (start timeout)");
                    mPendingCommandState.setTurningOn(false);
                    transitionTo(mBleOnState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_BLE_ON);
                    break;

                case ENABLE_TIMEOUT:
                    errorLog("Error enabling Bluetooth (enable timeout)");
                    mPendingCommandState.setBleTurningOn(false);
                    transitionTo(mOffState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                    break;

                case BREDR_STOP_TIMEOUT:
                    errorLog("Error stopping Bluetooth profiles (stop timeout)");
                    mPendingCommandState.setTurningOff(false);
                    transitionTo(mBleOnState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_BLE_ON);
                    break;

                case BLE_STOP_TIMEOUT:
                    errorLog("Error stopping Bluetooth profiles (BLE stop timeout)");
                    mPendingCommandState.setTurningOff(false);
                    transitionTo(mOffState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                    break;

                case DISABLE_TIMEOUT:
                    errorLog("Error disabling Bluetooth (disable timeout)");
                    if (isTurningOn)
                        mPendingCommandState.setTurningOn(false);
                    adapterService.stopProfileServices();
                    adapterService.stopGattProfileService();
                    mPendingCommandState.setTurningOff(false);
                    setBleTurningOff(false);
                    transitionTo(mOffState);
                    notifyAdapterStateChange(BluetoothAdapter.STATE_OFF);
                    break;

                default:
                    return false;
            }
            return true;
        }
}
