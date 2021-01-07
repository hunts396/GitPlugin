package com.kvk.plugins.git;

import org.jmock.Expectations;
import org.jmock.Mockery;

import org.kohsuke.github.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class FakeGitHubBuilder extends GitHubBuilder {
    public static final String AVATAR_URL = "https://avatars1.githubusercontent.com/u/53828084?s=400&v=4";
    public static final String TOKEN = "token";
    public static final String NOT_VALID_TOKEN = "not valid";
    public static final String LOGIN = "some login";
    protected List<GHIssue> allIssues;

    private Mockery ctx;
    private java.util.List<GHIssue> issuesList1;
    private List<GHIssue> issuesList2;

    private String currentToken;
    private GitHub gitHub;
    public FakeGitHubBuilder(){}

    public void setCtx(Mockery ctx) throws IOException {
        this.ctx = ctx;
        gitHub = createMockGitHub(ctx);
    }
    @Override
    public GitHubBuilder withOAuthToken(String oauthToken) {
        currentToken = oauthToken;
        return this;
    }

    @Override
    public GitHubBuilder withAbuseLimitHandler(AbuseLimitHandler handler) {
        return this;
    }

    @Override
    public GitHubBuilder withRateLimitHandler(RateLimitHandler handler) {
        return this;
    }

    @Override
    public GitHub build() throws IOException {
        if(!currentToken.equals(TOKEN))
            throw new IOException("credentials is not valid");
        return gitHub;
    }

    public GitHub createMockGitHub(Mockery ctx) throws IOException {

        GitHub mockGitHub = ctx.mock(GitHub.class);
        GHMyself mockMyself = ctx.mock(GHMyself.class);

        GHRepository mockRepository1 = ctx.mock(GHRepository.class, "repository1");
        GHRepository mockRepository2 = ctx.mock(GHRepository.class, "repository2");

        Map<String, GHRepository> repositories = new HashMap<>();
        repositories.put("some repo1", mockRepository1);
        repositories.put("some repo2", mockRepository2);

        GHIssue mockIssue1 = ctx.mock(GHIssue.class, "issue1");
        GHIssue mockIssue2 = ctx.mock(GHIssue.class, "issue2");
        allIssues = new ArrayList<>();
        issuesList1 = new ArrayList<>();
        issuesList1.add(mockIssue1);
        issuesList2 = new ArrayList<>();
        issuesList2.add(mockIssue2);

        Image avatar = ImageIO.read(new URL(AVATAR_URL));

        // overriding git api interface for plugin via jmock
        ctx.checking(new Expectations(){{
            allowing(mockGitHub).getMyself();will(returnValue(mockMyself));

            allowing(mockMyself).getLogin(); will(returnValue(LOGIN));
            allowing(mockMyself).getAvatarUrl(); will(returnValue(AVATAR_URL));
            allowing(mockMyself).getAllRepositories(); will(returnValue(repositories));

            allowing(mockRepository1).getIssues(GHIssueState.ALL); will(returnValue(issuesList2));
            allowing(mockRepository2).getIssues(GHIssueState.ALL); will(returnValue(issuesList1));

            allowing(mockIssue1).getTitle(); will(returnValue("title 1"));
            allowing(mockIssue2).getTitle(); will(returnValue("title 2"));
        }});
        allIssues.addAll(issuesList1);
        allIssues.addAll(issuesList2);
        allIssues.sort(Comparator.comparing(GHIssue::getTitle));
        return mockGitHub;
    }
    // add fake issue
    public void addMockIssue(String title){
        GHIssue mockIssue = ctx.mock(GHIssue.class, title);
        ctx.checking(new Expectations(){{
            allowing(mockIssue).getTitle(); will(returnValue(title));
        }});
        Boolean a = new Random().nextBoolean();
        if (a) issuesList1.add(mockIssue); else issuesList2.add(mockIssue);
        allIssues.add(mockIssue);
        allIssues.sort(Comparator.comparing(GHIssue::getTitle));
    }

    public List<GHIssue> getAllIssues() {
        return allIssues;
    }
}
