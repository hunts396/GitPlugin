package com.kvk.plugins.git.gui.menu;

import org.kohsuke.github.GHIssue;

import javax.swing.*;
import java.awt.*;

public class IssueCellRenderer extends JLabel implements ListCellRenderer<GHIssue> {


    @Override
    public Component getListCellRendererComponent(JList<? extends GHIssue> list, GHIssue value, int index, boolean isSelected, boolean cellHasFocus) {
        //JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // disable selecting because of auto refresh
        setText(value.getTitle());
        return this;
    }
}
