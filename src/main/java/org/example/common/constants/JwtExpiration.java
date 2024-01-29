package org.example.common.constants;

public final class JwtExpiration {

    //기간 30분 30*60*1000
    public static final int ACCESS_TOKEN_EXPIRATION = 30*60*1000;
    //기간 1주일
    public static final int REFRESH_TOKEN_EXPIRATION = 7*24*60*60*1000;
}
