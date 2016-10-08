package cn.spinsoft.wdq.bean;

import java.io.Serializable;

/**
 * Created by hushujun on 15/12/16.
 */
public class SimpleResponse implements Serializable {
    public static final int SUCCESS = 0;
    private int code;
    private String message;
    private int contentInt;
    private String contentString;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getContentInt() {
        return contentInt;
    }

    public void setContentInt(int contentInt) {
        this.contentInt = contentInt;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    @Override
    public String toString() {
        return "SimpleResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", contentInt='" + contentInt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleResponse response = (SimpleResponse) o;

        if (code != response.code) return false;
        return !(message != null ? !message.equals(response.message) : response.message != null);

    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
