class clientIdChanged {
public void clientIdChanged(ClientModel client, String newClientId) {
        logger.debugf("Updating clientId from '%s' to '%s'", client.getClientId(), newClientId);

        UserModel serviceAccountUser = realmManager.getSession().users().getServiceAccount(client);
        if (serviceAccountUser != null) {
            String username = ServiceAccountConstants.SERVICE_ACCOUNT_USER_PREFIX + newClientId;
            serviceAccountUser.setUsername(username);
            serviceAccountUser.setEmail(username + "@placeholder.org");
        }
    }
}
