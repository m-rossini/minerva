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
 * Created on 20/12/2005
 */
package br.com.auster.minerva.pentaho.test.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author framos
 * @version $Id$
 */
public class SomeBean implements Serializable {

	
	private int yearsOfWork;
	private String name;
	private String surrName;
	private Date birthDate;
	
	public SomeBean() {
	}
	
	public int getYearsOfWork() {
		return this.yearsOfWork;
	}
	
	public void setYearsOfWork(int _years) {
		this.yearsOfWork = _years;
	}
	
	public String getSurrName() {
		return this.surrName;
	}
	
	public void setSurrName(String _name) {
		this.surrName = _name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String _name) {
		this.name = _name;
	}
	
	public Date getBirthdate() {
		return this.birthDate;
	}
	
	public void setBirthdate(Date _date) {
		this.birthDate = _date;
	}
	
}


