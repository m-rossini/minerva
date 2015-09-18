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
 * Defines which report implementation should be executed, in order to fullfil an incoming
 * 	request. The means by which this selection is made should be discussed and detailed in 
 * 	each implmentation of this interface.
 * <p>
 * By Minerva environment, there is just ONE instance of ReportFactory in place.
 * 
 * @author framos
 * @version $Id$
 */
public interface ReportFactory {

    /**
     * Returns the report implementation selected for the specified request. If none was selected, 
     * 	then <code>null</code> is returned.
     *  
     * @param _request the incoming report request
     * 
     * @return the report implementation to be run
     */
    public Report getReport(ReportRequest _request);
    
    /**
     * Checks if there is a match (i.e., a report implementation) for the current report request. If 
     * 	so, then @link #getReport(ReportRequest) should return a not-null instance; otherwise, it will
     *  return <code>null</code>.
     *    
     * @param _request the incoming report request
     * 
     * @return wether or not there is a matching report for the request
     */
    public boolean isReportAvailable(ReportRequest _request);
       
    /**
     * Initializes the instance of ReportFactory
     * 
     * @param _configuration the factory implementation, in XML format
     * 
     * @throws ReportInitializationException if, by some reason, the factory could not be initialized
     */
    public void configure(Element _configuration)  throws ReportInitializationException;
}
