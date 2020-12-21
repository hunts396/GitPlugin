package com.kvk.plugins.git.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.kvk.plugins.git.GPApiForIDEAInt;
import com.kvk.plugins.git.api.MockGitPluginTest;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.intellij.testFramework.EditorActionTestCase;

import java.io.IOException;

public class GPActionTest extends MockGitPluginTest {


    private GPApiForIDEAInt api;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        api = createMockGitApiForIdea();
    }

    @Test
    public void testBlockingAction() throws IOException {
        // test for empty credentials
        api.removeCredentials();
        GPAction gitProfileAction = new GPAction();
        gitProfileAction.setGitApi(api);
        boolean blocked = gitProfileAction.isBlocked();

        Assert.assertFalse(blocked);

        // test for not valid credentials
        // connect will be successful, but credentials wrong
        api.createCredentials(api.connect(TOKEN), NOT_VALID_TOKEN);
        blocked = gitProfileAction.isBlocked();

        Assert.assertFalse(blocked);

        // test for valid credentials
        api.createCredentials(api.connect(TOKEN), TOKEN);
        blocked = gitProfileAction.isBlocked();

        Assert.assertTrue(blocked);
    }


}