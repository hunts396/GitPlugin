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



    public GitAccountWindow(GitHub gitHub) throws IOException {
        super(true);
        init();

        setTitle("Git Profile");
        mainPanel.setPreferredSize(new Dimension(500, 500));
        setResizable(false);

        GHMyself myself = gitHub.getMyself();
        ImageIcon avatarIcon = new ImageIcon(
                GPApiForIDEA.resizeAvatar(
                        ImageIO.read(new URL(myself.getAvatarUrl()))
                )
        );

        gitUsername.setText(myself.getLogin());
        gitUsername.setIcon(avatarIcon);
        setAssignedIssues(myself);
    }


    private void setAssignedIssues(GHMyself myself) throws IOException {
        Map<String, GHRepository> allRepositories = myself.getAllRepositories();
        for (Map.Entry<String, GHRepository> rep : allRepositories.entrySet()){
            issuesModel.addAll(rep.getValue().getIssues(GHIssueState.ALL));
        }
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
