package com.contoso;

import com.microsoft.graph.logger.DefaultLogger;

import java.io.*;
import java.util.Properties;

public class OAuthProperties extends Properties {

    private final String filePath = System.getProperty("user.dir");

    private String appId;

    private String[] appScopes;

    private String appAccessToken;

    public OAuthProperties() {
        this.checkPropertiesFile();
        this.loadProperties();
        this.getAccessToken();
    }

    // Check out the properties file exist or not;
    private void checkPropertiesFile() {
        File file = new File(filePath+ "\\oAuth.properties");
        if (!file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath + "\\oAuth.properties", false);
                setProperty("app.id", "YOUR_CLIENT_ID");
                setProperty("app.scopes", "User.Read,Calendars.Read");
                store(fileOutputStream, "NOTE: Please make sure your application program is available.\n"
                    + "The detail you can realize on https://docs.microsoft.com/zh-CN/graph/tutorials/java?tutorial-step=2.");
                fileOutputStream.close();
            } catch (Exception e) {
                System.out.println("The 'oAuth.properties' file can't create on your program directory. Please check out your directory permissions.");
            } finally {
                System.out.println("The 'oAuth.properties' file created. Please setting it according the notes in file.");
                System.exit(0);
            }
        } else {
            System.out.println("The 'oAuth.properties' file exist. Continue.");
        }
    }

    // Load OAuth settings
    public void loadProperties() {
        try {
            // oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
            load(new InputStreamReader(new BufferedInputStream(new FileInputStream(filePath+ "\\oAuth.properties"))));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
        }
        this.setAppId(getProperty("app.id"));
        this.setAppScopes(getProperty("app.scopes").split(","));
    }

    // Get an access token
    public void getAccessToken() {
        if (!containsKey("app.accessToken")) {
            System.out.println("Get access token from authentication.");
            Authentication.initialize(this.getAppId());
            this.setAppAccessToken(Authentication.getUserAccessToken(this.getAppScopes()));
            if (!this.getAppAccessToken().equals(null)) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(filePath + "\\oAuth.properties", false);
                    setProperty("app.accessToken", this.getAppAccessToken());
                    store(fileOutputStream, null);
                    fileOutputStream.close();
                } catch (Exception e) {
                    return;
                }
            }
        } else {
            System.out.println("Get access token by 'oAuth.properties' file.");
            this.setAppAccessToken(getProperty("app.accessToken"));
        }
    }

    // Delete the access token
    public void deleteAcessToken() {
        if (containsKey("app.accessToken")) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath + "\\oAuth.properties", false);
                remove("app.accessToken");
                store(fileOutputStream,null);
                fileOutputStream.close();
            } catch (Exception e) {
                return;
            }
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String[] getAppScopes() {
        return appScopes;
    }

    public void setAppScopes(String[] appScopes) {
        this.appScopes = appScopes;
    }

    public String getAppAccessToken() {
        return appAccessToken;
    }

    public void setAppAccessToken(String appAccessToken) {
        this.appAccessToken = appAccessToken;
    }
}
