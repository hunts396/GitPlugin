package com.kvk.plugins.git.api;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class BrowserRouter {
    private URI url;

    public BrowserRouter(String url) {
        this.url = URI.create(url);
    }
    public void setUrl(String url){
        this.url = URI.create(url);
    }

    public void route() throws IOException {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)){
            desktop.browse(url);
        }
    }
}
