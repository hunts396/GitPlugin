package com.kvk.plugins.git.gui.settings;

import com.kvk.plugins.git.FakeGitHubBuilder;
import com.kvk.plugins.git.api.GitCredentials;
import com.kvk.plugins.git.api.MockGitPluginTest;
import com.kvk.plugins.git.gui.ImageComparator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GPSettingsWindowTest extends MockGitPluginTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setCtx();

    }

    @Test
    public void testInitGitHubUI() throws IOException {
        GitCredentials c = new GitCredentials(FakeGitHubBuilder.LOGIN, FakeGitHubBuilder.TOKEN);
        if(c.isPerformed())
            c.save();
        else return;

        GPSettingsWindow window = new GPSettingsWindow();
        Image avatar = getAvatar();


        checkAccount(window.getTextLabel());
        Assert.assertEquals(window.getTextLabel().getText(), FakeGitHubBuilder.LOGIN);
        Image windAvatarImage = ((ImageIcon)window.getTextLabel().getIcon()).getImage();
        Assert.assertEquals(new ImageComparator().compare(avatar, windAvatarImage), 0);

        Assert.assertEquals(window.getTokenTextField().getText(), FakeGitHubBuilder.TOKEN);

    }

    @Test
    public void testInitGitHubUIWithoutCredentials() {
        GitCredentials c = GitCredentials.get();
        c.clear();
        c.save();
        GPSettingsWindow window = new GPSettingsWindow();

        checkEmptyAccount(window);
    }


    @Test
    public void testApply() throws IOException {
        // apply to set credentials with not valid token
        GitCredentials c = GitCredentials.get();
        c.clear();
        c.save();
        GPSettingsWindow window = new GPSettingsWindow();
        window.getTokenTextField().setText(FakeGitHubBuilder.NOT_VALID_TOKEN);

        //expectedEx.expect(RuntimeException.class);
        //expectedEx.expectMessage("Can not connect to github account\ncredentials is not valid");
        try {
            window.apply();
        } catch (RuntimeException e){
            Assert.assertEquals(e.getMessage(), "Can not connect to github account\ncredentials is not valid");
        }

        //Assert.assertTrue(ShowErrorDialogAction.isShown());
        Assert.assertEquals(window.getTokenTextField().getText(), "");

        // apply to set credentials with valid token
        window.getTokenTextField().setText(FakeGitHubBuilder.TOKEN);
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
        Assert.assertNull(windAvatar);

        Assert.assertEquals(window.getTokenTextField().getText(), "");
    }

}