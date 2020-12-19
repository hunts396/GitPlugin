package com.kvk.plugins.git.actions;

import com.intellij.credentialStore.Credentials;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.Presentation;
import com.kvk.plugins.git.GPApiForIDEA;
import com.kvk.plugins.git.GPApiForIDEAInt;
import com.kvk.plugins.git.gui.menu.GitAccountWindow;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.*;


import java.io.*;

public class GPAction extends AnAction {


    // Make menu item inactive if account is not logged in
    private GPApiForIDEAInt gitApi = GPApiForIDEA.getInstance();
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation p = e.getPresentation();
        if(gitApi.getCredentials() == null)
            p.setEnabled(false);
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
