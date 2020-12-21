package com.kvk.plugins.git.api.actions.credentials;

import com.intellij.credentialStore.Credentials;
import org.hamcrest.Description;
import org.jmock.api.Invocation;
import org.kohsuke.github.GitHub;

public class CreateCredentialsAction extends CredentialsAction{
    @Override
    public void describeTo(Description description) {
        description.appendText("Creates default credentials");
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        GitHub gitHub = (GitHub) invocation.getParameter(0);
        String token = (String) invocation.getParameter(1);
        credentials = new Credentials(gitHub.getMyself().getLogin(), token);
        return null;
    }
}
