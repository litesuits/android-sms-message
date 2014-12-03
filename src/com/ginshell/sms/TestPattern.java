package com.ginshell.sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MaTianyu
 * @date 14-7-23
 */
public class TestPattern {
    public static final String template = "【bong】请转发以下内容至用户手机:13567813868 内容如下: 【bong】573511(bong验证码)填写入验证码框，继续下一步。退订回复TD";
    public static final String pattern  = "【bong】请转发以下内容至用户手机:(\\d{11}) 内容如下:(.+)退订回复TD";

    public static void main(String[] args) {
        Matcher matcher = Pattern.compile(pattern).matcher(template);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }

}
