/*
 * COPYRIGHT LICENSE: This information contains sample code provided in source code form. You may copy, modify, and distribute these
 * sample programs in any form without payment to IBM for the purposes of developing, using, marketing or distributing application
 * programs conforming to the application programming interface for the operating platform for which the sample code is written.
 * Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY,
 * SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE
 * SAMPLE SOURCE CODE. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE
 * SOURCE CODE. (C) Copyright IBM Corp. 2001, 2013. All Rights Reserved. Licensed Materials - Property of IBM.
 */

package wasdev.sample.jms.mdb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@MessageDriven
public class SampleMDB implements MessageListener {

	@Resource
	MessageDrivenContext ejbcontext;

	@PostConstruct
	public void postConstruct() {
	}

	@Override
	public void onMessage(Message message) {
		System.out.println("**************************************************************************");
		try {
			System.out.println("Message Received in Server MDB !!!" + message);
			// send message to MDBREPLYQ
			SendMDBResponse(message);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		System.out.println("**************************************************************************");
		System.out.println(" ");
	}

	public static void SendMDBResponse(Message msg) throws Exception {
		System.out.println("Test testQueueSendMessageMDB Started from MDB");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = null;
		try {
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("jms/qm");
			jmsContext = connectionFactory.createContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		Queue queue = (Queue) ctx.lookup("jms/MDBREPLYQ");
		JMSProducer producer = jmsContext.createProducer();
		producer.send(queue, msg);
		System.out.println("Sent reply message to " + queue + " with CorrelationId: " + msg.getJMSCorrelationID());
		
		System.out.println("Test testQueueSendMessageMDB Completed");
	}
	
}
