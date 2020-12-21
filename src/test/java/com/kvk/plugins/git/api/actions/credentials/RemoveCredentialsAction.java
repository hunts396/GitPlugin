package com.kvk.plugins.git.api.actions.credentials;

import org.hamcrest.Description;
import org.jmock.api.Invocation;

public class RemoveCredentialsAction extends CredentialsAction{

    @Override
    public void describeTo(Description description) {
        description.appendText("removes default credentials");
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        credentials = null;
        return null;
    }
}
