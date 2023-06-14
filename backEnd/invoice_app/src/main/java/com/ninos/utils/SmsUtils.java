package com.ninos.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsUtils {

    public static final String FROM_NUMBER = "+18555756371";
    public static final String SID_KEY = "AC9f707f9af2fce7eea0aa86fe4b7fc478";
    public static final String TOKEN_KEY = "";

    public static void sendSMS(String to, String messageBody){
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = Message.creator(new PhoneNumber("+" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }

}
