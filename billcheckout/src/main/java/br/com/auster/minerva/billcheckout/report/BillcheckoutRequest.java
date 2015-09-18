/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 19/10/2006
 */
package br.com.auster.minerva.billcheckout.report;

import br.com.auster.minerva.spi.ReportRequestBase;

/**
 * @author framos
 * @version $Id$
 */
public class BillcheckoutRequest extends ReportRequestBase {


	private String ruleId;
	private String ruleName;
	private String generatedFile;


	public static final String RULE_CODE = "RULE_CODE";
	public static final String TRANSACTION_ID = "TRANSACTION_ID";
	public static final String RECORD_COUNT = "RECORD_COUNT";


	public BillcheckoutRequest(String _reportName) {
		super(_reportName);
	}


	public String getGeneratedFile() {
		return this.generatedFile;
	}
	public void setGeneratedFile(String _file) {
		this.generatedFile = _file;
	}

	public String getRuleId() {
		return this.ruleId;
	}
	public void setRuleId(String _id) {
		this.ruleId = _id;
		this.getAttributes().put(RULE_CODE, _id);
	}

	public String getRuleName() {
		return this.ruleName;
	}
	public void setRuleName(String _name) {
		this.ruleName = _name;
	}

	public void setTransactionId(String _transactionId) {
		super.setTransactionId(_transactionId);
		this.getAttributes().put(TRANSACTION_ID, _transactionId);
	}

	public String getRecordCount() {
		return (String) this.getAttributes().get(RECORD_COUNT);
	}

	public void setRecordCount(int _count) {
		this.getAttributes().put(RECORD_COUNT, String.valueOf(_count));
	}

}

