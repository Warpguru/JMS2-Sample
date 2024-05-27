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
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JMSSampleP2P
 */
@WebServlet("/JMSSampleP2P")
public class JMSSampleP2P extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JMSSampleP2P() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String strAction = request.getParameter("ACTION");
		PrintWriter out = response.getWriter();
		try {
			if ("sendAndReceive".equalsIgnoreCase(strAction)) {
				// call the Send and Receive Message
				sendAndReceive(request, response);
			} else if ("sendMessage".equalsIgnoreCase(strAction)) {
				// Send Message only
				sendMessage(request, response);
			} else if ("receiveAllMessages".equalsIgnoreCase(strAction)) {
				// Receive All messages from queue
				receiveAllMessages(request, response);
			} else if ("receiveAllMessagesSelectors".equalsIgnoreCase(strAction)) {
				// receive all the messages using message selector
				receiveAllMessagesSelectors(request, response);
			} else if ("mdbRequestResponse".equalsIgnoreCase(strAction)) {
				// Send message to be processed by MDB and wait from MDB
				// response
				mdbRequestResponse(request, response);
			} else {
				out.println("JMS Sample - Client");
				out.println("");
				out.println("Incorrect Action Specified, please specify the Action");
				out.println("Example : http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=sendAndReceive");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=sendMessage");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=receiveAllMessages");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=receiveAllMessagesSelectors");
				out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=mdbRequestResponse");
			}
		} catch (Exception e) {
			out.println("Something unexpected happened, check the logs or restart the server");
			e.printStackTrace();
		}

	}

	/**
	 * Scenario: Point to Point </br> 
	 * Connects using connection factory jms/qm </br>
	 * Sends one message to Queue jms/INPUT_Q </br>
	 * Receives the message and prints it on console </br>
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws Exception
	 *             if an error occurs.
	 */

	public void sendAndReceive(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Client: SendAndReceive Started");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");

		// Lookup queue from JNDI
		Queue queue = (Queue) ctx.lookup("jms/INPUT_Q");
		Message msg = jmsContext.createTextMessage("JMS Client: Liberty Sample Message");
		msg.setJMSCorrelationID(UUID.randomUUID().toString());
		JMSProducer producer = jmsContext.createProducer();
		producer.send(queue, msg);
		out.println("Sent message with CorrelationId: " + msg.getJMSCorrelationID());
		JMSConsumer consumer = jmsContext.createConsumer(queue);
		msg = consumer.receiveNoWait();
		if (msg != null) {
			out.println("Received  message: " + msg);
			out.println();
		}
		consumer.close();

		out.println("SendAndReceive Completed");
	}// end of SendAndReceive

	/**
	 * Scenario: Point to Point </br> 
	 * Connects to connection factory jms/qm </br>
	 * Sends one message to Queue jms/INPUT_Q </br>
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws Exception
	 *             if an error occurs.
	 */

	public void sendMessage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Client: SendMessage Started");

		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");
		
		// Lookup queue from JNDI
		Queue queue = (Queue) ctx.lookup("jms/INPUT_Q");
		Message msg = jmsContext.createTextMessage("JMS Client: Liberty Sample Message");
		msg.setJMSCorrelationID(UUID.randomUUID().toString());
		msg.setStringProperty("COLOR", "BLUE");
		JMSProducer producer = jmsContext.createProducer();
		producer.send(queue, msg);
		out.println("Sent message with CorrelationId: " + msg.getJMSCorrelationID());
		
		out.println("SendMessage Completed");
	}// end of SendMessage

	/**
	 * Scenario: Point to Point </br>
	 * Connects to connection factory jms/qm </br>
	 * Sends one message to the queue jms/INPUT_Q </br>
	 * Receives all the messages from the above Queue and prints them on console </br>
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws Exception
	 *             if an error occurs.
	 */

	public void receiveAllMessages(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Client: ReceiveAllMessages Started");
		
		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");
		
		// Lookup queue from JNDI
		Queue queue = (Queue) ctx.lookup("jms/INPUT_Q");
		JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);

		out.println("Waiting for messages (2s timeout)");
		receiveMessage(out, jmsConsumer);
		
		jmsConsumer.close();
		
		out.println("ReceiveAllMessages Completed");
	} // end of ReceiveAllMessages

	/**
	 * Scenario: Point to Point </br>
	 * Connects to connection factory jms/qm </br>
	 * Sends one message to queue jms/INPUT_Q </br> 
	 * Receives the messages with selector COLOR='BLUE' and prints them on console </br>
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws Exception
	 *             if an error occurs.
	 */

	public void receiveAllMessagesSelectors(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Client: ReceiveAllMessagesSelectors Started");

		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");
		
		// Lookup queue from JNDI
		Queue queue = (Queue) ctx.lookup("jms/INPUT_Q");
		JMSConsumer jmsConsumer = jmsContext.createConsumer(queue, "COLOR='BLUE'");

		out.println("Waiting for messages (2s timeout)");
		receiveMessage(out, jmsConsumer);
		
		jmsConsumer.close();
		
		out.println("ReceiveAllMessagesSelectors Completed");
	} // end of ReceiveAllMessagesSelectors

	/**
	 * Scenario: Point to Point, Works in Conjunction with MDB </br>
	 * Send a message to a queue jms/MDBQ </br>
	 * MDB receives the Message </br>
	 * MDB will send that received message to jms/MDBREPLYQ </br> 
	 * This sample will then receive the message from jms/MDBREPLYQ queue </br>
	 * 
	 * NOTE: Ensure the MDB is running before running testMDB test case.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void mdbRequestResponse(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Client: MDBRequestResponse Started");

		InitialContext ctx = new InitialContext();
		JMSContext jmsContext = retrieveJmsContext(ctx, "jms/qm");
		
		// Lookup queue from JNDI
		Queue queue = (Queue) ctx.lookup("jms/MDBQ");
		Message msg = jmsContext.createTextMessage("JMS Client: MDB Test - Message to MDB");
		msg.setJMSCorrelationID(UUID.randomUUID().toString());
		msg.setStringProperty("COLOR", "BLUE");
		JMSProducer producer = jmsContext.createProducer();
		producer.send(queue, msg);
		out.println("Sent message to " + queue + " with CorrelationId: " + msg.getJMSCorrelationID());
		
		// Waiting for the MDB to process the message and send the reply message
		// Receive the message from MDBREPLYQ to validate the test scenario
		queue = (Queue) ctx.lookup("jms/MDBREPLYQ");
		JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
		out.println("Waiting for messages in " + queue + " (2s timeout)");
		boolean messageReceived = receiveMessage(out, jmsConsumer);
		jmsConsumer.close();
		
		if (!messageReceived) {
			throw new Exception("MDB did not receive the Message");
		} else {
			out.println("MDB has successfully received the message");
		}
		
		out.println("MDBRequestResponse Completed");
	}// end of MDBRequestResponse

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
