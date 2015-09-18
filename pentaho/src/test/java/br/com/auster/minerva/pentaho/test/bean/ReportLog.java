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
 * Created on 26/12/2005
 */
package br.com.auster.minerva.pentaho.test.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author framos
 * @version $Id$
 */
public class ReportLog implements Serializable {

	
	private List times;
	private List files;
	
	public ReportLog() {
	}
	
	public void setExecTimes(List _times) {
		this.times = _times;
	}
	
	public List getExecTimes() {
		return this.times;
	}
	
	public void setFilenames(List _files) {
		this.files = _files;		
	}
	
	public List getFilenames() {
		return this.files;
	}
	
	
	public String toString() {
		return "[ReportLog]" + " created files " + this.files + " with times " + this.times;
	}
	
}
