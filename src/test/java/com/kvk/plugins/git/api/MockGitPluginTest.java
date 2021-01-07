package com.kvk.plugins.git.api;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.kvk.plugins.git.FakeGitHubBuilder;
import com.kvk.plugins.git.gui.ImageComparator;
import com.kvk.plugins.git.gui.images.DefaultAvatarSettings;
import com.kvk.plugins.git.gui.images.ImageResizer;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.kohsuke.github.GitHubBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public abstract class MockGitPluginTest extends BasePlatformTestCase {

    protected Mockery ctx = new JUnit4Mockery(){{
        setThreadingPolicy(new Synchroniser());
        setImposteriser(ClassImposteriser.INSTANCE);
    }};


    public void setCtx() throws IOException {
        ((FakeGitHubBuilder)ServiceManager.getService(GitHubBuilder.class)).setCtx(ctx);
    }

    public Image getAvatar() throws IOException {
        return ImageResizer.resizeImage(
                ImageIO.read(new URL(FakeGitHubBuilder.AVATAR_URL)),
                DefaultAvatarSettings.WIDTH,
                DefaultAvatarSettings.HEIGHT
        );
    }






    // validate login and avatar
    public void checkAccount(String login, Image avatar) throws IOException {
        Assert.assertEquals(login, FakeGitHubBuilder.LOGIN);
        Assert.assertEquals(new ImageComparator().compare(avatar, getAvatar()), 0);
    }

    // validate label, login - text of label, avatar - label icon
    public void checkAccount(JLabel label) throws IOException {
        checkAccount(label.getText(), ((ImageIcon) label.getIcon()).getImage());
    }
    public void addIssueToGitHub(String title){
        ((FakeGitHubBuilder)ServiceManager.getService(GitHubBuilder.class)).addMockIssue(title);
    }
}
