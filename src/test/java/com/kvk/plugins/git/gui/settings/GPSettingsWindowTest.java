package com.kvk.plugins.git.gui.settings;

import com.kvk.plugins.git.GPApiForIDEAInt;
import com.kvk.plugins.git.api.MockGitPluginTest;
import com.kvk.plugins.git.api.actions.exceptions.ShowErrorDialogAction;
import com.kvk.plugins.git.gui.ImageComparator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GPSettingsWindowTest extends MockGitPluginTest {
    private GPApiForIDEAInt api;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        api = createMockGitApiForIdea();
    }

    @Test
    public void testInitGitHubUI() throws IOException {
        api.createCredentials(api.connect(TOKEN), TOKEN);
        GPSettingsWindow window = new GPSettingsWindow(api);
        Image avatar = getAvatar();


        checkAccount(window.getTextLabel());
        Assert.assertEquals(window.getTextLabel().getText(), LOGIN);
        Image windAvatarImage = ((ImageIcon)window.getTextLabel().getIcon()).getImage();
        Assert.assertEquals(new ImageComparator().compare(avatar, windAvatarImage), 0);

        Assert.assertEquals(window.getTokenTextField().getText(), TOKEN);

    }

    @Test
    public void testInitGitHubUIWithoutCredentials() throws IOException {
        api.removeCredentials();
        GPSettingsWindow window = new GPSettingsWindow(api);

        checkEmptyAccount(window);
    }


    @Test
    public void testApply() throws IOException {
        // apply to set credentials with not valid token
        api.removeCredentials();
        GPSettingsWindow window = new GPSettingsWindow(api);
        window.getTokenTextField().setText(NOT_VALID_TOKEN);
        window.apply();

        Assert.assertTrue(ShowErrorDialogAction.isShown());
        Assert.assertEquals(window.getTokenTextField().getText(), "");

        // apply to set credentials with valid token
        window.getTokenTextField().setText(TOKEN);
        window.apply();

        checkAccount(window.getTextLabel());
        Assert.assertEquals(window.getRefToBrowsLabel().getText(), "");

        // apply to remove credentials
        window.getRemoveButton().doClick();
        window.apply();
        checkEmptyAccount(window);

    }


    // check ui for empty credentials
    public void checkEmptyAccount(GPSettingsWindow window){
        Assert.assertEquals(window.getTextLabel().getText(), "No accounts here");
        Assert.assertEquals(window.getRefToBrowsLabel().getText(), "<html><a href=\"#\">create token</a></html");
        Icon windAvatar = window.getTextLabel().getIcon();
        Assert.assertEquals(windAvatar, null);

        Assert.assertEquals(window.getTokenTextField().getText(), "");
    }

}