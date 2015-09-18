/*
/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on Sep 15, 2005
 */
package br.com.auster.minerva.jms;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.minerva.core.MinervaException;
import br.com.auster.minerva.core.MinervaManager;
import br.com.auster.minerva.core.ReportInitializationException;
import br.com.auster.minerva.core.RequestListener;

/**
 * 
 * <code>
 * &lt;configuration topic-name="" topic-factory=""&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;jndi-properties&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;property name=""&gt;...&lt;/property&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;property name=""&gt;...&lt;/property&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(...)<br>
 * &nbsp;&nbsp;&nbsp;&lt;/jndi-properties&gt;<br>
 * &lt;/configuration&gt;<br>
 * </code>
 * 
 * @author framos
 * @version $Id$
 */
public class JMSRequestListener implements RequestListener {


	
	// ----------------------------
	// Class constants
	// ----------------------------
	
    protected static final Log log = LogFactory.getLog(JMSRequestListener.class);
    protected static final I18n i18n = I18n.getInstance(JMSRequestListener.class);

    
    
	// ----------------------------
	// Instance variables
	// ----------------------------
    
    protected MinervaManager manager;
    
    
    protected Hashtable jndiLookup;
    protected String topicFactoryJNDI;
    protected String topicNameJNDI;

    private TopicConnection topicConnection;
    
    protected MessageListener messageHandler;

    
    
	// ----------------------------
	// Interface methods
	// ----------------------------
    
    /**
     * @see br.com.auster.minerva.core.RequestListener#setManager(br.com.auster.minerva.core.MinervaManager)
     */
    public void setManager(MinervaManager _manager) {
        manager = _manager;
        this.messageHandler = new JMSMessageHandler(_manager);
    }

    /**
     * @see br.com.auster.minerva.core.RequestListener#configure(org.w3c.dom.Element)
     */
    public void configure(Element _configuration) throws ReportInitializationException {
        
        topicFactoryJNDI = DOMUtils.getAttribute(_configuration, "topic-factory", true);
        topicNameJNDI = DOMUtils.getAttribute(_configuration, "topic-name", true);
        log.debug(i18n.getString("listener.config.topic", topicFactoryJNDI, topicNameJNDI));
        
        jndiLookup = new Hashtable();
        NodeList jndiProperties = DOMUtils.getElements(DOMUtils.getElement(_configuration, "jndi-properties", true), "property");
        for (int i=0; i < jndiProperties.getLength(); i++) {
            Element property = (Element) jndiProperties.item(i);
            jndiLookup.put(DOMUtils.getAttribute(property, "name", true), DOMUtils.getText(property).toString());
        }
        log.debug(i18n.getString("listener.config.jndiProps", jndiLookup));
    }

    /**
     * @see br.com.auster.minerva.core.RequestListener#listen()
     */
    public void listen() {
        try {
            Context jndiContext = new InitialContext(jndiLookup);
            TopicConnectionFactory connectionFactory = (TopicConnectionFactory) jndiContext.lookup(topicFactoryJNDI);
            Topic topic = (Topic) jndiContext.lookup(topicNameJNDI);
            topicConnection = connectionFactory.createTopicConnection();
            log.debug(i18n.getString("listener.connected", topicNameJNDI));
            TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);            
            topicSubscriber.setMessageListener(messageHandler);
            topicConnection.start();            
            log.debug(i18n.getString("listener.listening", topicNameJNDI));
        } catch (NamingException ne) {
            throw new MinervaException(ne);
        } catch (JMSException jmse) {
            throw new MinervaException(jmse);
        }
    }
    
    /**
     * @see br.com.auster.minerva.core.RequestListener#stop()
     */
    public void stop() {
        try {
            log.info(i18n.getString("listener.stoping", topicNameJNDI));
            topicConnection.close();
        } catch (JMSException jmse) {
            log.warn(i18n.getString("listener.stop.error", topicNameJNDI), jmse);
        }
        topicConnection = null;
        this.notifyAll();

    }
}
