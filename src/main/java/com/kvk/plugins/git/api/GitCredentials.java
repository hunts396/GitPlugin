package com.kvk.plugins.git.api;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class GitCredentials {
    private Credentials credentials;
    private static final String SERVICE_NAME = "git-token:GitPlugin";

    public GitCredentials(String login, String token){
        credentials = new Credentials(login, token);

    }

    private GitCredentials(Credentials credentials){
        this.credentials = credentials;
    }

    public String getLogin(){
        return credentials.getUserName();
    }
    public String getToken(){ return credentials.getPasswordAsString(); }

    public void setData(String login, String token){
        credentials = new Credentials(login, token);
    }

    public void save(){
        CredentialAttributes attributes = new CredentialAttributes(SERVICE_NAME);
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    public void clear(){
        credentials = null;
    }

    public boolean isPerformed(){
        return credentials != null && getLogin() != null && getToken() != null;
    }

    public boolean isEstablished(){
        Credentials savedCredentials = PasswordSafe.getInstance().get(new CredentialAttributes(SERVICE_NAME));
        return (credentials == null && savedCredentials == null) || credentials.equals(savedCredentials);
    }

    public static GitCredentials get(){
        return new GitCredentials(PasswordSafe.getInstance().get(new CredentialAttributes(SERVICE_NAME)));
    }
}
