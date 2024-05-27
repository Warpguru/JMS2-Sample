/*
 * COPYRIGHT LICENSE: This information contains sample code provided in source code form.
 * You may copy, modify, and distribute these sample programs in any form without payment
 * to IBM for the purposes of developing, using, marketing or distributing application
 * programs conforming to the application programming interface for the operating platform
 * for which the sample code is written.
 *
 * Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON
 * AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY,
 * SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR
 * CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF
 * THE SAMPLE SOURCE CODE. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 *
 * (C) Copyright IBM Corp. 2001, 2021.
 * All Rights Reserved. Licensed Materials - Property of IBM.
 */


package wasdev.sample.jms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JMSSamplePubSub
 */
@WebServlet("/JMSSamplePubSub")
public class JMSSamplePubSub extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JMSSamplePubSub() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String strAction = request.getParameter("ACTION");
		PrintWriter out = response.getWriter();
		try{
			if("nonDurableSubscriber".equalsIgnoreCase(strAction)){
				// Create a non durable subscriber and publish and receive the message from topic
				nonDurableSubscriber(request, response);
			}else if("durableSubscriber".equalsIgnoreCase(strAction)){
				// Create a Durable subscriber and publish and receive the message from topic
				durableSubscriber(request, response);
			}else if("publishMessages".equalsIgnoreCase(strAction)){
				// Publish 5 messages to the topic
				publishMessages(request, response);
			}else if("unsubscribeDurableSubscriber".equalsIgnoreCase(strAction)){
				// Unsubscribe the registered durable subscriber
				unsubscribeDurableSubscriber(request, response);
			}else{
				out.println("JMS Sample - Server");
				out.println("");
				out.println("Incorrect Action Specified, please specify the Action");
				out.println("Example : http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=nonDurableSubscriber");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=durableSubscriber");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=publishMessages");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=unsubscribeDurableSubscriber");
			}

		}catch(Exception e){
			out.println("Something unexpected happened, check the logs or restart the server");
			e.printStackTrace();
		}
	}

	/**
	 * Scenario: Performs Non-Durable pub/sub flow </br>
	 * Connects to connection factory jms/qm </br>
	 * Creates a NON-Durable subscriber for topic jms/jmsTopic </br>
	 * Publishes a single message to the topic jmsTopic </br>
	 * Subscriber receives the message from topic jmsTopic and the message is printed on console </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void nonDurableSubscriber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Server: NonDurableSubscriber Started");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");

		// Lookup topic from JNDI
		Topic topic = (Topic) ctx.lookup("jms/jmsTopic");
		JMSConsumer jmsConsumer = jmsContext.createConsumer(topic);

		out.println("Waiting for messages (2s timeout)");
		receiveMessage(out, jmsConsumer);

		jmsConsumer.close();
		
		out.println("NonDurableSubscriber Completed");
	} // NonDurableSubscriber

	/**
	 * Test scenario: Performs Durable pub/sub flow </br>
	 * Connects to connection factory jms/qm </br>
	 * Creates durable subscriber(named DURATEST) for topic jmsTopic </br>
	 * Publishes a single message to the topic jms/jmsTopic </br>
	 * Subscriber receives the message from topic jms/jmsTopic </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void durableSubscriber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Server: DurableSubscriber Started");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");
		
		// Lookup topic from JNDI
		Topic topic = (Topic) ctx.lookup("jms/jmsTopic");
		JMSConsumer jmsConsumer = jmsContext.createDurableConsumer(topic, "DURATEST");

		out.println("Waiting for messages (2s timeout)");
		receiveMessage(out, jmsConsumer);

		jmsConsumer.close();

		out.println("DurableSubscriber Completed");
	}// end of DurableSubscriber

	/**
	 * Test scenario: Publish messages to Topic </br>
	 * Connects to connection factory jms/qm </br>
	 * Publishes 5 messages to the topic jms/jmsTopic </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void publishMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Server: PublishMessage Started");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");

		// Lookup topic from JNDI
		Topic topic = (Topic) ctx.lookup("jms/jmsTopic");
		JMSProducer jmsProducer = jmsContext.createProducer();
		
		// send 5 messages
		int msgs = 5;
		for (int i = 0; i < msgs; i++) {
			Message msg = jmsContext.createTextMessage("JMS Server: Liberty PubSub Message : " + (i + 1));
			msg.setJMSCorrelationID(UUID.randomUUID().toString());
			msg.setStringProperty("COLOR", "BLUE");
			jmsProducer.send(topic, msg);
			out.println("Published message: " + (i + 1) + " with CorrelationId: " + msg.getJMSCorrelationID());
		}
		out.println(msgs+ " Messages published");
		
		out.println("PublishMessage Completed");
	}// PublishMessage


	/**
	 * Test scenario: Unsubscribe the durable subscriber </br>
	 * Connects to connection factory jms/qm </br>
	 * Creates/Opens durable subscriber (named DURATEST) for topic jms/jmsTopic </br>
	 * Consumes all messages to the topic jms/jmsTopic </br>
	 * Subscriber unsubscribes from topic jms/jmsTopic </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void unsubscribeDurableSubscriber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Server: UnsubscribeDurableSubscriber Started");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");
		
		// Lookup topic from JNDI
		Topic topic = (Topic) ctx.lookup("jms/jmsTopic");
		JMSConsumer jmsConsumer = jmsContext.createDurableConsumer(topic, "DURATEST");

		// Consume all the existing messages for durable subscriber DURATEST
		out.println("Waiting for messages (2s timeout)");
		receiveMessage(out, jmsConsumer);

		jmsConsumer.close();

		jmsContext.unsubscribe("DURATEST");

		out.println("UnsubscribeDurableSubscriber Completed");
	}//UnsubscribeDurableSubscriber

	/**
	 * Retrieve a {@link JMSContext} for the {@link ConnectionFactory} referenced in JNDI by {@code jndiNameConnectionFactory}.
	 * 
	 * @param ctx
	 * @param jndiNameConnectionFactory
	 * @return {@link JMSContext}
	 */
	private JMSContext retrieveJmsContext(final InitialContext ctx, final String jndiNameConnectionFactory) {
		JMSContext jmsContext = null;
		try {
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup(jndiNameConnectionFactory);
			jmsContext = connectionFactory.createContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return jmsContext;
	}

	/**
	 * Retrieve the next message(s) from a {@link JMSConsumer}, wait up to {@code 2 seconds} for a message.
	 * 
	 * @param out
	 * @param jmsConsumer
	 * @return {@code messageReceived} flag
	 */
	private boolean receiveMessage(final PrintWriter out, final JMSConsumer jmsConsumer) {
		Message msg = null;
		boolean messageReceived = false;
		int retryCount = 0;
		do {
			if (retryCount == 0) {
				msg = jmsConsumer.receiveNoWait();
			}
			if ((msg == null) || (retryCount > 0)) {
				msg = jmsConsumer.receive(2000);
			}
			if (msg != null) {
				out.println("Received  message: " + msg);
				out.println();
				messageReceived = true;
			}
			retryCount++;
		} while (msg != null);
		return messageReceived;
	}

}
