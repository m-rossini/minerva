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
 * Created on Sep 25, 2005
 */
package br.com.auster.minerva.jms;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.auster.common.util.I18n;
import br.com.auster.minerva.core.MinervaManager;
import br.com.auster.minerva.spi.ReportRequest;

/**
 * @author framos
 * @version $Id$
 */
public class JMSMessageHandler implements MessageListener {



	// ----------------------------
	// Class constants
	// ----------------------------
    
    protected static final Log log = LogFactory.getLog(JMSMessageHandler.class);
    protected static final I18n i18n = I18n.getInstance(JMSMessageHandler.class);

    protected static final Set<String> alreadyProcesed = new HashSet<String>();
    
    
    
	// ----------------------------
	// Instance variables
	// ----------------------------
	
    private MinervaManager manager;

    
    
	// ----------------------------
	// Constructors
	// ----------------------------
    
    public JMSMessageHandler(MinervaManager _manager) {
        manager = _manager;
    }
    
    
    
	// ----------------------------
	// Interface methods
	// ----------------------------
    
    /**
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message _message) {
        try {
            if (_message instanceof ObjectMessage) {
                ReportRequest request = (ReportRequest) ((ObjectMessage)_message).getObject();
                log.info(i18n.getString("handler.message", request.getTransactionId()));
                // TODO
                // This check is purely due to problems in WL. 
                // This wont be necessary in the new environment (lifecycle) 
                boolean byPassDupCheck = false;
                if (request.getAttributes() != null) {
                	log.trace("We do have Attributes in the request.");
                	byPassDupCheck = request.getAttributes().containsKey(ReportRequest.ATTR_BYPASS_DUP_CHECK);	
                }
                log.trace("After getting flag. ByPassDupCheck IS:" + byPassDupCheck);
                
                boolean previouslyExecuted = alreadyProcesed.contains(request.getTransactionId());
                if (!byPassDupCheck) {                
                	alreadyProcesed.add(request.getTransactionId());
                }
                // releasing message
                _message.acknowledge();
                if (previouslyExecuted && !byPassDupCheck) {
                	log.warn(i18n.getString("handler.discarding", request.getTransactionId()));
                } else {
	                if (!manager.enqueueRequest(request)) {
	                    log.error(i18n.getString("handler.messageNotEnqueued"));
	                }
                }
            } else {
                log.error(i18n.getString("handler.messageNotCompatible"));
            }
        } catch (JMSException jmse) {
            log.error(i18n.getString("handler.transmitionError"), jmse);
        }
    }

}
