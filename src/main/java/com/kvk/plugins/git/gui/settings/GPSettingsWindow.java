package com.kvk.plugins.git.gui.settings;

import com.intellij.credentialStore.Credentials;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBLabel;
import com.kvk.plugins.git.GPApiForIDEA;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GPSettingsWindow implements Configurable {
    private JPanel mainPanel;
    private JTextField tokenTextField;
    private JLabel refToBrowsLabel;
    private JLabel textLabel;
    private JButton removeButton;

    private boolean isRemoved;
    private String prevToken = "";

    public GPSettingsWindow() {


        Credentials c = GPApiForIDEA.getCredentials();
        GHMyself myself = null;
        if (c != null) {
            prevToken = c.getPasswordAsString();
            try {

                GitHub gitHub = GPApiForIDEA.connect(prevToken);
                myself = gitHub.getMyself();
                tokenTextField.setText(prevToken);
            } catch (IOException e) {
                GPApiForIDEA.removeCredentials();
            }
        }
        try {
            setAccountInfo(myself);
        } catch (IOException ignored) {}
        removeButton.addActionListener(e -> {
            prevToken = "";
            isRemoved = true;
            try {
                setAccountInfo(null);
            } catch (IOException ignored) {
            }
        });


    }

    /*
    UI Account settings
    if account removed or not logged in - delete all text from tokenTextField and offer user to create token
    if account is logged in - show avatar and username, paste his personal token
    */
    protected void setAccountInfo(GHMyself myself) throws IOException {

        if (myself != null) {
            textLabel.setText(myself.getLogin());
            textLabel.setIcon(
                    new ImageIcon(
                            GPApiForIDEA.resizeAvatar(ImageIO.read(new URL(myself.getAvatarUrl())))
                    )
            );
            refToBrowsLabel.setText("");
            refToBrowsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            textLabel.setText("No accounts here");
            refToBrowsLabel.setText("<html><a href=\"#\">create token</a></html");
            refToBrowsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            refToBrowsLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    GPApiForIDEA.redirectToGeneratingToken();
                }
            });
            tokenTextField.setText("");
        }
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Git Profile";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return !prevToken.equals(tokenTextField.getText()) || isRemoved;
    }

    @Override
    public void apply() {
        if (isRemoved) {
            GPApiForIDEA.removeCredentials();
            isRemoved = false;
        }


        String newToken = tokenTextField.getText();
        if (!newToken.equals(prevToken)) {
            try {
                GitHub gitHub = GPApiForIDEA.connect(newToken);
                GPApiForIDEA.createCredentials(gitHub, newToken);
                setAccountInfo(gitHub.getMyself());
                prevToken = newToken;
            } catch (IOException e) {
                GPApiForIDEA.showConnectErrorMessage(e);
                tokenTextField.setText(prevToken);
            }
        }
    }

}
