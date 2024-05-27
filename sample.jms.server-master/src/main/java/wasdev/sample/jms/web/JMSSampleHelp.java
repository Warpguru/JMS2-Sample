package wasdev.sample.jms.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JMSSampleHelp
 */
@WebServlet("/JMSSampleHelp")
public class JMSSampleHelp extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JMSSampleHelp() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("JMS Sample - Server");
		out.println("");
		out.println("APIs for testing Topic messages:");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=nonDurableSubscriber");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=durableSubscriber");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=publishMessages");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSamplePubSub?ACTION=unsubscribeDurableSubscriber");
		out.println("APIS for testing Queue messages:");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=sendAndReceive");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=sendMessage");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=receiveAllMessages");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=receiveAllMessagesSelectors");
		out.println("          http://<host>:<port>/jms2-JMSSample/JMSSampleP2P?ACTION=mdbRequestResponse");
	}
	
}
