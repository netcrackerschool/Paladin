package com.netcracker.paladin;

import com.netcracker.paladin.swing.SwingPaladinEmail;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created on 27.10.14.
 */
public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        SwingPaladinEmail swingEmailSender = context.getBean(SwingPaladinEmail.class);
        swingEmailSender.launch();
    }
}
