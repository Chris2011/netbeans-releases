/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testGenerateJavaEE70;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author {user}
 */
@MessageDriven(activationConfig =  {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "TestMessageDestination2")
    })
public class TestMDBQueueBean2 implements MessageListener {
    
    public TestMDBQueueBean2() {
    }

    @Override
    public void onMessage(Message message) {
    }
    
}
