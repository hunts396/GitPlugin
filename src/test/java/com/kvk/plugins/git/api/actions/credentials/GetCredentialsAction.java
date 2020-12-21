package com.kvk.plugins.git.api.actions.credentials;

import org.hamcrest.Description;
import org.jmock.api.Invocation;

public class GetCredentialsAction extends CredentialsAction{

    @Override
    public void describeTo(Description description) {
        description.appendText("returns credentials");
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        return credentials;
    }
}
