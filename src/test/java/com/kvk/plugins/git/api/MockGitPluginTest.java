package com.kvk.plugins.git.api;

import com.intellij.credentialStore.Credentials;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.kvk.plugins.git.GPApiForIDEA;
import com.kvk.plugins.git.GPApiForIDEAInt;
import com.kvk.plugins.git.api.actions.credentials.CreateCredentialsAction;
import com.kvk.plugins.git.api.actions.credentials.GetCredentialsAction;
import com.kvk.plugins.git.api.actions.credentials.RemoveCredentialsAction;
import com.kvk.plugins.git.api.actions.exceptions.ShowErrorDialogAction;
import com.kvk.plugins.git.gui.ImageComparator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.kohsuke.github.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public abstract class MockGitPluginTest extends BasePlatformTestCase {

    protected Mockery ctx = new JUnit4Mockery(){{
        setThreadingPolicy(new Synchroniser());
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    protected final String AVATAR_URL = "https://avatars1.githubusercontent.com/u/53828084?s=400&v=4";
    protected final String TOKEN = "token";
    protected final String NOT_VALID_TOKEN = "not valid";
    protected final String LOGIN = "some login";
    protected Credentials credentials;
    protected List<GHIssue> allIssues;
    private List<GHIssue> issuesList1;
    private List<GHIssue> issuesList2;

    public Image getAvatar() throws IOException {
        return GPApiForIDEA.getInstance().resizeAvatar(ImageIO.read(new URL(AVATAR_URL)));
    }


    // initializing fake github api for testing
    public GPApiForIDEAInt createMockGitApiForIdea() throws IOException {
        // creating mock objects
        GPApiForIDEAInt mockGitApi = ctx.mock(GPApiForIDEAInt.class);
        GitHub mockGitHub = ctx.mock(GitHub.class);
        GHMyself mockMyself = ctx.mock(GHMyself.class);

        GHRepository mockRepository1 = ctx.mock(GHRepository.class, "repository1");
        GHRepository mockRepository2 = ctx.mock(GHRepository.class, "repository2");

        Map<String, GHRepository> repositories = new HashMap<>();
        repositories.put("some repo1", mockRepository1);
        repositories.put("some repo2", mockRepository2);

        GHIssue mockIssue1 = ctx.mock(GHIssue.class, "issue1");
        GHIssue mockIssue2 = ctx.mock(GHIssue.class, "issue2");


        allIssues = new ArrayList<>();
        issuesList1 = new ArrayList<>();
        issuesList1.add(mockIssue1);
        issuesList2 = new ArrayList<>();
        issuesList2.add(mockIssue2);

        Image avatar = ImageIO.read(new URL(AVATAR_URL));

        // overriding git api interface for plugin via jmock
        ctx.checking(new Expectations(){{
            allowing(mockGitHub).getMyself();will(returnValue(mockMyself));

            allowing(mockGitApi).connect(TOKEN); will(returnValue(mockGitHub));
            allowing(mockGitApi).connect(NOT_VALID_TOKEN);
            will(throwException(new IOException("bad credentials")));

            allowing(mockGitApi).createCredentials(mockGitHub, TOKEN); will(new CreateCredentialsAction());
            allowing(mockGitApi).createCredentials(mockGitHub, NOT_VALID_TOKEN); will(new CreateCredentialsAction());

            allowing(mockGitApi).getCredentials(); will(new GetCredentialsAction());
            allowing(mockGitApi).removeCredentials(); will(new RemoveCredentialsAction());

            allowing(mockGitApi).resizeAvatar(with(any(Image.class)));
                will(returnValue(GPApiForIDEA.getInstance().resizeAvatar(avatar)));

            allowing(mockGitApi).showConnectErrorMessage(with(any(Exception.class)));
                will(new ShowErrorDialogAction());
            allowing(mockGitApi).showErrorMessage(with(any(Exception.class)));
                will(new ShowErrorDialogAction());


            allowing(mockMyself).getLogin(); will(returnValue(LOGIN));
            allowing(mockMyself).getAvatarUrl(); will(returnValue(AVATAR_URL));
            allowing(mockMyself).getAllRepositories(); will(returnValue(repositories));

            allowing(mockRepository1).getIssues(GHIssueState.ALL); will(returnValue(issuesList2));
            allowing(mockRepository2).getIssues(GHIssueState.ALL); will(returnValue(issuesList1));

            allowing(mockIssue1).getTitle(); will(returnValue("title 1"));
            allowing(mockIssue2).getTitle(); will(returnValue("title 2"));
        }});
        allIssues.addAll(issuesList1);
        allIssues.addAll(issuesList2);
        allIssues.sort(Comparator.comparing(GHIssue::getTitle));
        return mockGitApi;
    }

    // add fake issue
    public void addMockIssue(String title){
        GHIssue mockIssue = ctx.mock(GHIssue.class, title);
        ctx.checking(new Expectations(){{
            allowing(mockIssue).getTitle(); will(returnValue(title));
        }});
        Boolean a = new Random().nextBoolean();
        if (a) issuesList1.add(mockIssue); else issuesList2.add(mockIssue);
        allIssues.add(mockIssue);
        allIssues.sort(Comparator.comparing(GHIssue::getTitle));
    }

    // validate login and avatar
    public void checkAccount(String login, Image avatar) throws IOException {
        Assert.assertEquals(login, LOGIN);
        Assert.assertEquals(new ImageComparator().compare(avatar, getAvatar()), 0);
    }

    // validate label, login - text of label, avatar - label icon
    public void checkAccount(JLabel label) throws IOException {
        checkAccount(label.getText(), ((ImageIcon) label.getIcon()).getImage());
    }
}
