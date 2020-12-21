package com.kvk.plugins.git.api.actions.exceptions;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

// test action for exceptions handling
public class ShowErrorDialogAction implements Action {
    private static boolean shown;

    @Override
    public void describeTo(Description description) {
        description.appendText("setting isShown=true if dialog shown");
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        shown = true;
        return null;
    }
    public static boolean isShown(){
        boolean s = shown;
        reset();
        return s;
    }
    public static void reset(){
        shown = false;
    }
}
