/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 17/10/2006
 */
package br.com.auster.minerva.billcheckout;

import java.util.Hashtable;

import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import br.com.auster.minerva.billcheckout.test.SimpleReportRequest;
import br.com.auster.minerva.spi.ReportRequest;
import br.com.auster.minerva.spi.ReportRequestBase;

/**
 * @author framos
 * @version $Id$
 */
public class TopicSender {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TopicSender().send();
	}

	public TopicSender() {
		
	}
	
	public void send() {
		
		try { 
		    Hashtable env = new Hashtable();
		    env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		    env.put(Context.PROVIDER_URL, "t3://localhost:7001");
		    Context ctx =  new InitialContext(env);
		    
		    TopicConnectionFactory tconFactory = (TopicConnectionFactory) 
	      			PortableRemoteObject.narrow(ctx.lookup("billcheckout.TopicConnectionFactory"), TopicConnectionFactory.class);
		    TopicConnection tcon = tconFactory.createTopicConnection();
		    TopicSession tsession = tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		    Topic topic = (Topic) PortableRemoteObject.narrow(ctx.lookup("topic.ReportTopic"), Topic.class);
		    TopicPublisher tpublisher = tsession.createPublisher(topic);
		    ObjectMessage msg = tsession.createObjectMessage();
		    tcon.start();
	
		    msg.setObject(createTestRequest());
		    tpublisher.publish(msg);
			
		    tpublisher.close();
		    tsession.close();
		    tcon.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ReportRequest createTestRequest() {
		ReportRequestBase r = new SimpleReportRequest("simple-report");
		r.setTransactionId("68");
		return r;
	}
}




//package examples.jms.topic;
//
//import java.io.*;
//import java.util.*;
//import javax.transaction.*;
//import javax.naming.*;
//import javax.jms.*;
//import javax.rmi.PortableRemoteObject;
//
///**
// * This examples shows how to establish a connection and send messages to the
// * JMS topic. The classes in this package operate on the same topic. Run
// * the classes together to observe message being sent and received. This
// * class is used to send messages to
// * the topic.
// *
// * @author Copyright (c) 1999-2006 by BEA Systems, Inc. All Rights Reserved.
// */
//public class TopicSend
//{
//  // Defines the JNDI context factory.
//  public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
//  // Defines the JMS connection factory.
//  public final static String JMS_FACTORY="weblogic.examples.jms.TopicConnectionFactory";
//  // Defines the topic.
//  public final static String TOPIC="weblogic.examples.jms.exampleTopic";
//
//  protected TopicConnectionFactory tconFactory;
//  protected TopicConnection tcon;
//  protected TopicSession tsession;
//  protected TopicPublisher tpublisher;
//  protected Topic topic;
//  protected TextMessage msg;
//
//  /**
//   * Creates all the necessary objects for sending
//   * messages to a JMS Topic.
//   *
//   * @param ctx JNDI initial context
//   * @param topicName name of topic
//   * @exception NamingException if problem occurred with the JNDI context interface
//   * @exception JMSException if JMS fails to initialize due to internal error
//   *
//   */
//  public void init(Context ctx, String topicName)
//    throws NamingException, JMSException
//  {
//    tconFactory = (TopicConnectionFactory) 
//      PortableRemoteObject.narrow(ctx.lookup(JMS_FACTORY),
//                                  TopicConnectionFactory.class);
//    tcon = tconFactory.createTopicConnection();
//    tsession = tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
//    topic = (Topic) PortableRemoteObject.narrow(ctx.lookup(topicName), Topic.class);
//    tpublisher = tsession.createPublisher(topic);
//    msg = tsession.createTextMessage();
//    tcon.start();
//  }
//
//  /**
//   * Sends a message to a JMS topic.
//   *
//   * @param message message to be sent
//   * @exception JMSException if JMS fails to send message due to internal error
//   *
//   */
//  public void send(String message) throws JMSException {
//    msg.setText(message);
//    tpublisher.publish(msg);
//  }
//
//  /**
//   * Closes JMS objects.
//   *
//   * @exception JMSException if JMS fails to close objects due to internal error
//   */
//  public void close() throws JMSException {
//    tpublisher.close();
//    tsession.close();
//    tcon.close();
//  }
//
///**
//  * main() method.
//  *
//  * @param args WebLogic Server URL
//  * @exception Exception if operation fails
//  */
//  public static void main(String[] args) throws Exception {
//    if (args.length != 1) {
//      System.out.println("Usage: java examples.jms.topic.TopicSend WebLogicURL");
//      return;
//    }
//    InitialContext ic = getInitialContext(args[0]);
//    TopicSend ts = new TopicSend();
//    ts.init(ic, TOPIC);
//    readAndSend(ts);
//    ts.close();
//  }
//
// /**
//  * Prompts, reads, and sends a message.
//  *
//  * @param ts TopicSend
//  * @exception IOException if problem occurs during read/write operation
//  * @exception JMSException if JMS fails due to internal error
//  */
//  protected static void readAndSend(TopicSend ts)
//    throws IOException, JMSException
//  {
//    BufferedReader msgStream = new BufferedReader (new InputStreamReader(System.in));
//    String line=null;
//    do {
//      System.out.print("Enter message (\"quit\" to quit): \n");
//      line = msgStream.readLine();
//      if (line != null && line.trim().length() != 0) {
//        ts.send(line);
//        System.out.println("JMS Message Sent: "+line+"\n");
//      }
//    } while (line != null && ! line.equalsIgnoreCase("quit"));
//  }
//
///**
//  * Get initial JNDI context.
//  *
//  * @param url Weblogic URL.
//  * @exception  NamingException if problem occurs with JNDI context interface
//  */
//  protected static InitialContext getInitialContext(String url)
//    throws NamingException
//  {
//  }
//
//}
//
