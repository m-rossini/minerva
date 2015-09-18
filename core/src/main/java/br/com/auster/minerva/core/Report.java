/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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

import java.io.Serializable;

import org.w3c.dom.Element;

import br.com.auster.minerva.spi.ReportRequest;

/**
 * Interface representing how reports are executed. 
 * 
 * @author framos
 * @version $Id$
 */
public interface Report extends Cloneable, Serializable {

	/**
	 * Generates the report, according to the requested parameters.
	 * 
	 * @param _request the incoming request
	 * 
	 * @throws ReportException if, by some reason, the report was not generated
	 */
	public void generate(ReportRequest _request) throws ReportException;
	
	/**
	 * Initializes this report implementation with the necessary data.
	 * 
	 * @param _configuration the report configuration, in XML format.
	 * 
	 * @throws ReportInitializationException if, by some reason, the report could not be initialized
	 */
	public void configure(Element _configuration) throws ReportInitializationException; 

	/**
	 * @see java.lang.Object#clone();
	 */
	public Object clone();
	
}
