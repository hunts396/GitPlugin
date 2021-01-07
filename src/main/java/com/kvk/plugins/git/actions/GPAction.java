package com.kvk.plugins.git.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import com.kvk.plugins.git.api.GitCredentials;
import com.kvk.plugins.git.gui.menu.GitAccountWindow;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.*;


import java.io.*;

public class GPAction extends AnAction {


    // Make menu item inactive if account is not logged in
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isBlocked());
    }


    // returns true if credentials correct and false otherwise
    public boolean isBlocked() {

        GitCredentials c = GitCredentials.get();
        if (!c.isPerformed())
            return false;

        try {
            ServiceManager.getService(GitHubBuilder.class)
                    .withOAuthToken(c.getToken())
                    .withRateLimitHandler(RateLimitHandler.WAIT)
                    .withAbuseLimitHandler(AbuseLimitHandler.WAIT).build();
        } catch (IOException ioException) {
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        GitCredentials c = GitCredentials.get();
        if (!c.isPerformed())
            return;

        try {
            GitHub gitHub = ServiceManager.getService(GitHubBuilder.class)
                    .withOAuthToken(c.getToken())
                    .withRateLimitHandler(RateLimitHandler.WAIT)
                    .withAbuseLimitHandler(AbuseLimitHandler.WAIT).build();

            GitAccountWindow accountWindow = new GitAccountWindow(gitHub);
            accountWindow.showAndGet();

        } catch (IOException e) {
            Messages.showErrorDialog("Can not connect to github account\n" + e.getMessage(), "Error");
        }

    }


}
