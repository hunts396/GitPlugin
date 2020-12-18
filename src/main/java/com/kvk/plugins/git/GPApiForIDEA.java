package com.kvk.plugins.git;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import org.kohsuke.github.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

// class for solving simple git plugin tasks
public class GPApiForIDEA {
    private static final int AVATAR_WIDTH = 25, AVATAR_HEIGHT = 25;
    private static final URI CREATE_TOKEN_URI = URI.create("https://github.com/settings/tokens");
    private static final String SERVICE_NAME = "git-token:GitPlugin";

    public static GitHub connect(String token) throws IOException {
        GitHubBuilder ghb = new GitHubBuilder().withOAuthToken(token);
        ghb.withRateLimitHandler(RateLimitHandler.WAIT).
                withAbuseLimitHandler(AbuseLimitHandler.WAIT);
        return ghb.build();
    }

    public static void createCredentials(GitHub gitHub, String token) throws IOException {
        CredentialAttributes attributes = new CredentialAttributes(SERVICE_NAME);
        Credentials credentials = new Credentials(gitHub.getMyself().getLogin(), token);
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    public static void removeCredentials(){
        CredentialAttributes attributes = new CredentialAttributes(SERVICE_NAME);
        PasswordSafe.getInstance().set(attributes, null);
    }

    public static Credentials getCredentials(){
        CredentialAttributes savedAttributes = new CredentialAttributes(SERVICE_NAME);
        return PasswordSafe.getInstance().get(savedAttributes);
    }

    public static Image resizeAvatar(Image avatar){
        BufferedImage resizedAvatar = new BufferedImage(AVATAR_WIDTH, AVATAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedAvatar.createGraphics();
        g.drawImage(avatar, 0, 0, AVATAR_WIDTH, AVATAR_HEIGHT, null);
        g.dispose();
        return resizedAvatar;
    }

    public static void showConnectErrorMessage(Exception e){
        JOptionPane.showMessageDialog(
                null,
                "Can not connect to github account\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void redirectToGeneratingToken(){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)){
            try{
                desktop.browse(CREATE_TOKEN_URI);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error browsing git create token uri\n" + ioException.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

}
