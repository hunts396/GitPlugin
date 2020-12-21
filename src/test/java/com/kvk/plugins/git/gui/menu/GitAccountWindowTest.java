package com.kvk.plugins.git.gui.menu;

import com.kvk.plugins.git.GPApiForIDEA;
import com.kvk.plugins.git.GPApiForIDEAInt;
import com.kvk.plugins.git.api.MockGitPluginTest;
import com.kvk.plugins.git.gui.ImageComparator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.github.GHIssue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class GitAccountWindowTest extends MockGitPluginTest {

    GPApiForIDEAInt api;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        api = createMockGitApiForIdea();
    }

    @Test
    public void testInitUI() throws IOException {
        GitAccountWindow window = new GitAccountWindow(api.connect("token"), api);

        checkAccount(window.getGitUsername());

        // test for issues list
        Assert.assertEquals(window.getIssuesList().getModel(), window.getIssuesModel());
        Object[] windArray = ((DefaultListModel<GHIssue>)window.getIssuesList().getModel()).toArray();
        Assert.assertArrayEquals(getSortedIssuesArray(windArray), allIssues.toArray());

    }

    @Test
    public void testStartRefreshing() throws IOException {
        GitAccountWindow window = new GitAccountWindow(api.connect("token"), api);
        addMockIssue("title 3");
        try {
            Thread.sleep(3100);

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