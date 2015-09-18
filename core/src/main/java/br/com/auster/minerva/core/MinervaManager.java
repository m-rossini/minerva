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

import br.com.auster.minerva.spi.ReportRequest;

/**
 * Centralizer of all actions executed within the Minerna environment. Implementations of this 
 * 	interface will handle all the flow since the moment the request for generating a report was
 *  receveived, until its sent back, when necessary, to the original caller.
 * <p>
 * A single instance of any implementation is shared across all threads.  
 * 
 * @author framos
 * @version $Id$
 */
public interface MinervaManager {

	/**
	 * Enqueues a recently received request to be processed. This request will be sent to the 
	 * 	configured factory implementation, which will find the matching report implementation 
	 * 	fitted to this request. The selected report implementation will then be executed by
	 * 	the manager.
	 *  
	 * @param _request the report request received
	 * 
	 * @return if the report was generated successfuly
	 */
    public boolean enqueueRequest(ReportRequest _request);
    
    /**
     * Returns the least recently finished request for report generation. As the requests are executed 
     * 	they are organized in order of termination, so that dispatchers can fetch then and notify 
     *  the caller that the report was generated.
     *    
     * @return a report request, or <code>null</code> if none is on the finished queue
     */
    public ReportRequest nextFinishedRequest();
    
    /**
     * Performs initial configuration actions in the Manager instance.
     *  
     * @param _configuration the manager configuration, in XML format
     * 
     * @throws ReportInitializationException if something was not correctly configured
     */
    public void configure(Element _configuration) throws ReportInitializationException;
    
}
