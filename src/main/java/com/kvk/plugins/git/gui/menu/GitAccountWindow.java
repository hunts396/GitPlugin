package com.kvk.plugins.git.gui.menu;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.kvk.plugins.git.GPApiForIDEA;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GitAccountWindow extends DialogWrapper {
    private JPanel mainPanel;
    private JLabel gitUsername;
    private JBList<GHIssue> issuesList;
    private DefaultListModel<GHIssue> issuesModel;
    GHMyself myself;

    public GitAccountWindow(GitHub gitHub) {
        super(true);
        init();

        setTitle("Git Profile");
        mainPanel.setPreferredSize(new Dimension(500, 500));
        setResizable(false);
        try {
            myself = gitHub.getMyself();
            ImageIcon avatarIcon = new ImageIcon(
                    GPApiForIDEA.resizeAvatar(
                            ImageIO.read(new URL(myself.getAvatarUrl()))
                    )
            );

            gitUsername.setText(myself.getLogin());
            gitUsername.setIcon(avatarIcon);
            setAssignedIssues();
        } catch (IOException e){
            GPApiForIDEA.showConnectErrorMessage(e);
        }


        Thread refreshIssuesThread = new Thread(() -> {
            if(myself != null) {
                while (true) {
                    try {
                        setAssignedIssues();
                        Thread.sleep(3000);
                    } catch (IOException | InterruptedException e) {
                        GPApiForIDEA.showErrorMessage(e);
                    }
                }
            }
        });
        refreshIssuesThread.start();
    }


    private void setAssignedIssues() throws IOException {
        ArrayList<GHIssue> issues = new ArrayList<>();
        Map<String, GHRepository> allRepositories = myself.getAllRepositories();
        for (Map.Entry<String, GHRepository> rep : allRepositories.entrySet()) {
            issues.addAll(rep.getValue().getIssues(GHIssueState.ALL));
        }
        issuesModel.removeAllElements();
        issuesModel.addAll(issues);
                
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        issuesModel = new DefaultListModel<>();

        issuesList = new JBList<>(issuesModel);
        issuesList.setCellRenderer(new IssueCellRenderer());
        issuesList.setEmptyText("No assigned issues");
    }
}
