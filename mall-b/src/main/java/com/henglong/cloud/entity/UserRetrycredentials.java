package com.henglong.cloud.entity;

import java.util.Date;

public class UserRetrycredentials {
    private Integer retryCount;

    private Date Time;

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
        Time = time;
    }
}
