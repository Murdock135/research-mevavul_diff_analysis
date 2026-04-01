class createDialog {
@NonNull
    private Dialog createDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v).setPositiveButton(R.string.common_ok, null)
                .setNeutralButton(R.string.common_cancel, null)
                .setTitle(R.string.end_to_end_encryption_title);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        switch (keyResult) {
                            case KEY_CREATED:
                                Log_OC.d(TAG, "New keys generated and stored.");

                                dialog.dismiss();

                                Intent intentCreated = new Intent();
                                intentCreated.putExtra(SUCCESS, true);
                                intentCreated.putExtra(ARG_POSITION, getArguments().getInt(ARG_POSITION));
                                getTargetFragment().onActivityResult(getTargetRequestCode(),
                                        SETUP_ENCRYPTION_RESULT_CODE, intentCreated);
                                break;

                            case KEY_EXISTING_USED:
                                Log_OC.d(TAG, "Decrypt private key");

                                textView.setText(R.string.end_to_end_encryption_decrypting);

                                try {
                                    String privateKey = task.get();
                                    String mnemonicUnchanged = passwordField.getText().toString();
                                    String mnemonic = passwordField.getText().toString().replaceAll("\\s", "")
                                        .toLowerCase(Locale.ROOT);
                                    String decryptedPrivateKey = EncryptionUtils.decryptPrivateKey(privateKey,
                                                                                                   mnemonic);

                                    arbitraryDataProvider.storeOrUpdateKeyValue(user.getAccountName(),
                                                                                EncryptionUtils.PRIVATE_KEY, decryptedPrivateKey);

                                    dialog.dismiss();
                                    Log_OC.d(TAG, "Private key successfully decrypted and stored");

                                    arbitraryDataProvider.storeOrUpdateKeyValue(user.getAccountName(),
                                                                                EncryptionUtils.MNEMONIC,
                                                                                mnemonicUnchanged);

                                    // check if private key and public key match
                                    String publicKey = arbitraryDataProvider.getValue(user.getAccountName(),
                                                                                      EncryptionUtils.PUBLIC_KEY);

                                    byte[] key1 = generateKey();
                                    String base64encodedKey = encodeBytesToBase64String(key1);

                                    String encryptedString = EncryptionUtils.encryptStringAsymmetric(base64encodedKey,
                                                                                                     publicKey);
                                    String decryptedString = decryptStringAsymmetric(encryptedString,
                                                                                     decryptedPrivateKey);

                                    byte[] key2 = decodeStringToBase64Bytes(decryptedString);

                                    if (!Arrays.equals(key1, key2)) {
                                        throw new Exception("Keys do not match");
                                    }

                                    Intent intentExisting = new Intent();
                                    intentExisting.putExtra(SUCCESS, true);
                                    intentExisting.putExtra(ARG_POSITION, getArguments().getInt(ARG_POSITION));
                                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                                                                         SETUP_ENCRYPTION_RESULT_CODE, intentExisting);

                                } catch (Exception e) {
                                    textView.setText(R.string.end_to_end_encryption_wrong_password);
                                    Log_OC.d(TAG, "Error while decrypting private key: " + e.getMessage());
                                }
                                break;

                            case KEY_GENERATE:
                                passphraseTextView.setVisibility(View.GONE);
                                positiveButton.setVisibility(View.GONE);
                                neutralButton.setVisibility(View.GONE);
                                getDialog().setTitle(R.string.end_to_end_encryption_storing_keys);

                                GenerateNewKeysAsyncTask newKeysTask = new GenerateNewKeysAsyncTask();
                                newKeysTask.execute();
                                break;

                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
            }
        });
        return dialog;
    }
}
