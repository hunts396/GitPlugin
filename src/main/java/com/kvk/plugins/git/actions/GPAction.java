package com.kvk.plugins.git.actions;

import com.intellij.credentialStore.Credentials;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.Presentation;
import com.kvk.plugins.git.GPApiForIDEA;
import com.kvk.plugins.git.GPApiForIDEAInt;
import com.kvk.plugins.git.gui.menu.GitAccountWindow;
import git4idea.commands.Git;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.*;


import java.io.*;

public class GPAction extends AnAction {

    private GPApiForIDEAInt gitApi = GPApiForIDEA.getInstance();

    public void setGitApi(GPApiForIDEAInt api){
        gitApi = api;
    }

    // Make menu item inactive if account is not logged in
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isBlocked());
    }


    // returns true if credentials correct and false otherwise
    public boolean isBlocked(){
        Credentials c = gitApi.getCredentials();
        if(c == null) {
            return false;
        }
        try{
            gitApi.connect(c.getPasswordAsString());
        } catch (IOException ioException) {
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String token;
        Credentials c = gitApi.getCredentials();
        if(c != null)
            token = c.getPasswordAsString();
        else return;

        try {
            GitHub gitHub = gitApi.connect(token);

            GitAccountWindow accountWindow = new GitAccountWindow(gitHub);
            accountWindow.showAndGet();

        } catch (IOException ioException) {
            gitApi.showConnectErrorMessage(ioException);
        }

    }


}
