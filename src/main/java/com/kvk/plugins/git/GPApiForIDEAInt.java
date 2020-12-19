package com.kvk.plugins.git;

import com.intellij.credentialStore.Credentials;
import org.kohsuke.github.GitHub;

import java.awt.*;
import java.io.IOException;

public interface GPApiForIDEAInt {
    GitHub connect(String token) throws IOException;
    void createCredentials(GitHub gitHub, String token) throws IOException;
    void removeCredentials();
    Credentials getCredentials();
    Image resizeAvatar(Image avatar);
    void showConnectErrorMessage(Exception e);
    void showErrorMessage(Exception e);
    void redirectToGeneratingToken();

}
