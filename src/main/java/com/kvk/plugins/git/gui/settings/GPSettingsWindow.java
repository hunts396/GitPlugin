package com.kvk.plugins.git.gui.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import com.kvk.plugins.git.api.BrowserRouter;
import com.kvk.plugins.git.api.GitCredentials;
import com.kvk.plugins.git.gui.images.DefaultAvatarSettings;
import com.kvk.plugins.git.gui.images.ImageResizer;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

public class GPSettingsWindow implements Configurable {
    private static final String GENERATE_URL = "https://github.com/settings/tokens";

    private JPanel mainPanel;
    private JTextField tokenTextField;
    private JLabel refToBrowsLabel;
    private JLabel textLabel;
    private JButton removeButton;

    private boolean isRemoved;
    private String prevToken = "";
    private GHMyself myself;


    public GPSettingsWindow() {
        initGitHubUI();
        initUIComponents();
    }

    public void initGitHubUI(){
        GitCredentials c = GitCredentials.get();
        if (c.isPerformed()) {
            prevToken = c.getToken();
            try {

                GitHub gitHub = ServiceManager.getService(GitHubBuilder.class)
                        .withOAuthToken(c.getToken())
                        .withRateLimitHandler(RateLimitHandler.WAIT)
                        .withAbuseLimitHandler(AbuseLimitHandler.WAIT).build();

                myself = gitHub.getMyself();
                tokenTextField.setText(prevToken);
            } catch (IOException e) {
                c.clear();
                c.save();
            }
        }
        try {
            setAccountInfo();
        } catch (IOException ignored) {}
    }

    private void initUIComponents(){
        removeButton.addActionListener(e -> {
            prevToken = "";
            isRemoved = true;
            myself = null;
            try {
                setAccountInfo();
            } catch (IOException ignored) {
            }
        });
    }
    /*
    UI Account settings
    if account removed or not logged in - delete all text from tokenTextField and offer user to create token
    if account is logged in - show avatar and username, paste his personal token
    */
    public void setAccountInfo() throws IOException {

        if (myself != null) {
            textLabel.setText(myself.getLogin());
            textLabel.setIcon(
                    new ImageIcon(
                            ImageResizer.resizeImage(
                                    ImageIO.read(new URL(myself.getAvatarUrl())),
                                    DefaultAvatarSettings.WIDTH,
                                    DefaultAvatarSettings.HEIGHT
                            )
                    )
            );
            refToBrowsLabel.setText("");
            refToBrowsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            textLabel.setText("No accounts here");
            textLabel.setIcon(null);
            refToBrowsLabel.setText("<html><a href=\"#\">create token</a></html");
            refToBrowsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            refToBrowsLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        new BrowserRouter(GENERATE_URL).route();
                    } catch (IOException ioException) {
                        Messages.showErrorDialog(
                                "Error browsing git create token uri\n" + ioException.getMessage(),
                                "Error"
                                );
                    }

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
            //gitApi.removeCredentials();
            GitCredentials c = GitCredentials.get();
            c.clear();
            c.save();
            isRemoved = !c.isEstablished();
        }


        String newToken = tokenTextField.getText();
        if (!newToken.equals(prevToken)) {
            try {
                GitHub gitHub = ServiceManager.getService(GitHubBuilder.class)
                        .withOAuthToken(newToken)
                        .withRateLimitHandler(RateLimitHandler.WAIT)
                        .withAbuseLimitHandler(AbuseLimitHandler.WAIT).build();

                //gitApi.createCredentials(gitHub, newToken);
                GitCredentials c = new GitCredentials(gitHub.getMyself().getLogin(), newToken);
                if(c.isPerformed())
                    c.save();
                if(!c.isEstablished())
                    return;

                myself = gitHub.getMyself();
                setAccountInfo();
                prevToken = newToken;
            } catch (IOException e) {
                tokenTextField.setText(prevToken);
                Messages.showErrorDialog("Can not connect to github account\n" + e.getMessage(), "Error");
            }
        }
    }

    public JTextField getTokenTextField() {
        return tokenTextField;
    }

    public JLabel getRefToBrowsLabel() {
        return refToBrowsLabel;
    }

    public JLabel getTextLabel() {
        return textLabel;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

}
