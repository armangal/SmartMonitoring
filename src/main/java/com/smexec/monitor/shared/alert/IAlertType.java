package com.smexec.monitor.shared.alert;


public interface IAlertType {

    String getName();
    
    Integer getId();
    
    AlertGroup getAlertGroup();
    
    boolean sendMail();
    
    long getAlertFrequency();
}