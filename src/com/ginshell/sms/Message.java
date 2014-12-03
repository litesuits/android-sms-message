package com.ginshell.sms;

import com.litesuits.orm.db.annotation.PrimaryKey;

import java.util.Date;

/**
 * @author MaTianyu
 * @date 14-7-23
 */
public class Message {
    public String phone;
    public String body;
    public Date time;

    @PrimaryKey(PrimaryKey.AssignType.AUTO_INCREMENT)
    public long id;


}
