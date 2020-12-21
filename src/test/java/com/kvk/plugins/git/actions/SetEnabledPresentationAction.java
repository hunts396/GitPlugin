package com.kvk.plugins.git.actions;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class SetEnabledPresentationAction implements Action{
    private static boolean enabled;
    @Override
    public void describeTo(Description description) {
        description.appendText("set enabled or disabled account dialog window");
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        enabled = (boolean) invocation.getParameter(0);
        return null;
    }
    public static boolean isEnabled(){
        return enabled;
    }
}
