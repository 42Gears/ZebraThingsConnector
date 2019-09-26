package com.gears42.zebrademo1;

import org.mozilla.iot.webthing.CredentialsIx;

/**
 * Model to store things server credentials
 **/
public class Credentials implements CredentialsIx {
    private String email;
    private String password;
    private String key;

    public Credentials(final String email, final String password, final String key) {
        this.email = email;
        this.password = password;
        this.key = key;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(final String key) {
        this.key = key;

    }
}
