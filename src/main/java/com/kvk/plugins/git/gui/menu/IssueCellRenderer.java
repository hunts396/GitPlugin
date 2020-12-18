package com.kvk.plugins.git.gui.menu;

import com.intellij.ui.components.JBLabel;
import org.kohsuke.github.GHIssue;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class IssueCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setText(((GHIssue) value).getBody());
        return label;
    }

}
