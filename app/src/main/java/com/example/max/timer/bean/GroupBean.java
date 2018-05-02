package com.example.max.timer.bean;

import java.io.Serializable;

/**
 * Created by 贺石骞 on 2018/5/2.
 */

public class GroupBean implements Serializable {
    private String name;
    private String hash;

    @Override
    public String toString() {
        return "GroupBean{" +
                "name='" + name + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public GroupBean(String name, String hash) {

        this.name = name;
        this.hash = hash;
    }
}
