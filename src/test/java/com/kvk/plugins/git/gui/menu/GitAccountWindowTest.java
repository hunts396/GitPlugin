package com.kvk.plugins.git.gui.menu;

import com.intellij.openapi.components.ServiceManager;
import com.kvk.plugins.git.FakeGitHubBuilder;
import com.kvk.plugins.git.api.MockGitPluginTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GitHubBuilder;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GitAccountWindowTest extends MockGitPluginTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
        setCtx();

    }

    @Test
    public void testInitUI() throws IOException {
        GitAccountWindow window = new GitAccountWindow(ServiceManager.getService(GitHubBuilder.class)
                .withOAuthToken(FakeGitHubBuilder.TOKEN).build());

        checkAccount(window.getGitUsername());

        List<GHIssue> allIssues = ((FakeGitHubBuilder)ServiceManager.getService(GitHubBuilder.class)).getAllIssues();
        // test for issues list
        Assert.assertEquals(window.getIssuesList().getModel(), window.getIssuesModel());
        Object[] windArray = ((DefaultListModel<GHIssue>)window.getIssuesList().getModel()).toArray();
        Assert.assertArrayEquals(getSortedIssuesArray(windArray), allIssues.toArray());

    }

    @Test
    public void testStartRefreshing() throws IOException {
        GitAccountWindow window = new GitAccountWindow(ServiceManager.getService(GitHubBuilder.class)
                .withOAuthToken(FakeGitHubBuilder.TOKEN).build());

        addIssueToGitHub("title 3");
        try {
            Thread.sleep(3100);

            List<GHIssue> allIssues = ((FakeGitHubBuilder)ServiceManager.getService(GitHubBuilder.class)).getAllIssues();

            Object[] windArray = ((DefaultListModel<GHIssue>)window.getIssuesList().getModel()).toArray();
            Assert.assertArrayEquals(getSortedIssuesArray(windArray), allIssues.toArray());
        } catch (InterruptedException ignored) {}

    }



    public Object[] getSortedIssuesArray(Object[] array){

        Arrays.sort(
                array,
                Comparator.comparing(a -> ((GHIssue) a).getTitle())
        );
        return array;
    }

}