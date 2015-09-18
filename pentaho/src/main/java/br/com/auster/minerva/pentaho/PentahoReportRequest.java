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
 * Created on 19/12/2005
 */
package br.com.auster.minerva.pentaho;

import br.com.auster.minerva.interfaces.ReportRequestBase;

/**
 * @author framos
 * @version $Id$
 */
public class PentahoReportRequest extends ReportRequestBase {

	
	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
	private String solution;
	private String path;
	
	
	
	// ----------------------------
	// Constructors
	// ----------------------------
	
	public PentahoReportRequest(String _name) {
		this("", "", _name);
	}
	
	public PentahoReportRequest(String _solution, String _path, String _name) {
		super(_name);
		this.solution = _solution;
		this.path = _path;
	}
	
	
	
	// ----------------------------
	// Public methods
	// ----------------------------
	
	public void setSolution(String _solution) {
		this.solution = _solution;
	}
	
	public String getSolution() {
		return this.solution;
	}
	
	public void setReportPath(String _path) {
		this.path = _path;
	}
	
	public String getReportPath() {
		return this.path;
	}
	
}
