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

import org.w3c.dom.Element;

/**
 * @author framos
 * @version $Id$
 */
public class DispatcherThread implements Runnable {


	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
    private RequestDispatcher dispatcher;
    
    
    
	// ----------------------------
	// Constructors
	// ----------------------------
    
    public DispatcherThread(MinervaManager _manager, String _dispatcherClass, Element _configuration) throws ReportInitializationException {
        try {
            dispatcher = (RequestDispatcher) Class.forName(_dispatcherClass).newInstance();
            dispatcher.setManager(_manager);
            dispatcher.configure(_configuration);
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
        if (dispatcher == null) {
            throw new IllegalStateException();
        }
        dispatcher.dispatch();
    }

}
