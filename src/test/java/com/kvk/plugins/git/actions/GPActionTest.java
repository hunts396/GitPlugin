package com.kvk.plugins.git.actions;

import com.kvk.plugins.git.FakeGitHubBuilder;
import com.kvk.plugins.git.api.GitCredentials;
import com.kvk.plugins.git.api.MockGitPluginTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.io.IOException;

public class GPActionTest extends MockGitPluginTest {


    @Before
    public void setUp() throws Exception {

        super.setUp();
        setCtx();

    }

    @Test
    public void testBlockingAction() {
        // test for empty credentials
        GitCredentials c = GitCredentials.get();
        c.clear();
        c.save();
        GPAction gitProfileAction = new GPAction();
        boolean blocked = gitProfileAction.isBlocked();

        Assert.assertFalse(blocked);

        // test for not valid credentials
        // connect will be successful, but credentials wrong
        c = new GitCredentials(FakeGitHubBuilder.LOGIN, FakeGitHubBuilder.NOT_VALID_TOKEN);
        if(c.isPerformed())
            c.save();

        blocked = gitProfileAction.isBlocked();

        Assert.assertFalse(blocked);

        // test for valid credentials
        c = new GitCredentials(FakeGitHubBuilder.LOGIN, FakeGitHubBuilder.TOKEN);
        if(c.isPerformed())
            c.save();
        blocked = gitProfileAction.isBlocked();

        Assert.assertTrue(blocked);
    }


}