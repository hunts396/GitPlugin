package com.kvk.plugins.git.gui.menu;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.kvk.plugins.git.gui.images.DefaultAvatarSettings;
import com.kvk.plugins.git.gui.images.ImageResizer;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class GitAccountWindow extends DialogWrapper {
    private JPanel mainPanel;
    private JLabel gitUsername;
    private JBList<GHIssue> issuesList;
    private DefaultListModel<GHIssue> issuesModel;
    private GHMyself myself;


    public GitAccountWindow(GitHub gitHub) {
        super(true);
        init();
        initAll(gitHub);

    }
    public void initAll(GitHub gitHub){
        initMainUI();
        initGitUI(gitHub);
        startRefreshing();
    }

    public void initMainUI(){
        setTitle("Git Profile");
        mainPanel.setPreferredSize(new Dimension(500, 500));
        setResizable(false);
    }
    public void initGitUI(GitHub gitHub){
        try {
            myself = gitHub.getMyself();
            ImageIcon avatarIcon = new ImageIcon(
                    ImageResizer.resizeImage(
                            ImageIO.read(new URL(myself.getAvatarUrl())),
                            DefaultAvatarSettings.WIDTH,
                            DefaultAvatarSettings.HEIGHT
                    )
            );

            gitUsername.setText(myself.getLogin());
            gitUsername.setIcon(avatarIcon);
            setAssignedIssues();
        } catch (IOException e){
            Messages.showErrorDialog("Can not connect to github account\n" + e.getMessage(), "Error");
        }
    }

    public void startRefreshing(){
        Thread refreshIssuesThread = new Thread(() -> {
            if(myself != null) {
                while (true) {
                    try {
                        setAssignedIssues();
                        Thread.sleep(3000);
                    } catch (IOException | InterruptedException e) {

                        Messages.showErrorDialog(e.getMessage(), "Error");
                    }
                }
            }
        });
        refreshIssuesThread.start();
    }

    public void setAssignedIssues() throws IOException {
        ArrayList<GHIssue> issues = new ArrayList<>();
        Map<String, GHRepository> allRepositories = myself.getAllRepositories();
        for (Map.Entry<String, GHRepository> rep : allRepositories.entrySet()) {
            issues.addAll(rep.getValue().getIssues(GHIssueState.ALL));
        }
        issuesModel.removeAllElements();
        issuesModel.addAll(issues);

    }


    public void createUIComponents() {
        issuesModel = new DefaultListModel<>();

        issuesList = new JBList<>(issuesModel);
        issuesList.setCellRenderer(new IssueCellRenderer());
        issuesList.setEmptyText("No assigned issues");
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    public JLabel getGitUsername() {
        return gitUsername;
    }

    public JBList<GHIssue> getIssuesList() {
        return issuesList;
    }

    public DefaultListModel<GHIssue> getIssuesModel() {
        return issuesModel;
    }

}
