package com.kvk.plugins.git.api.actions.credentials;

import com.intellij.credentialStore.Credentials;
import org.jmock.api.Action;

// action for fake storage of credentials
public abstract class CredentialsAction implements Action {
    protected static Credentials credentials;

    public static Credentials getCredentials() {
        return credentials;
    }

    public static void setCredentials(Credentials credentials) {
        CredentialsAction.credentials = credentials;
    }
}
