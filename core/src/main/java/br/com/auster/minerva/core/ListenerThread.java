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
package br.com.auster.minerva.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import br.com.auster.common.util.I18n;

/**
 * @author framos
 * @version $Id$
 */
public class ListenerThread implements Runnable {

    
	
	// ----------------------------
	// Class constants
	// ----------------------------
	
    protected static final Log log = LogFactory.getLog(ListenerThread.class);
    protected static final I18n i18n = I18n.getInstance(ListenerThread.class);

    

	// ----------------------------
	// Instance variables
	// ----------------------------
    
    private RequestListener listener;    
    
    
    
	// ----------------------------
	// Constructors
	// ----------------------------

    public ListenerThread(MinervaManager _manager, String _listenerClass, Element _configuration) throws ReportInitializationException {
        try {
            log.debug(i18n.getString("listener.thread.init", _listenerClass));
            listener = (RequestListener) Class.forName(_listenerClass).newInstance();
            listener.setManager(_manager);
            listener.configure(_configuration);
            log.trace("Listener Class:" + this.listener.getClass().getName());
        } catch (ClassNotFoundException cnfe) {
            throw new ReportInitializationException(cnfe);
        } catch (InstantiationException ie) {
            throw new ReportInitializationException(ie);
        } catch (IllegalAccessException iae) {
            throw new ReportInitializationException(iae);
        }
    }
    
    
    
	// ----------------------------
	// Interface methods
	// ----------------------------
    
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if (listener == null) {
            log.error(i18n.getString("listener.thread.cannotStart"));
            throw new IllegalStateException();
        }
        log.info(i18n.getString("listener.thread.start"));
        listener.listen();
    }

}
