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
 * Created on 10/10/2006
 */
package br.com.auster.minerva.billcheckout.report;

import java.util.List;

import junit.framework.TestCase;
import br.com.auster.minerva.csv.report.QueryProcessor;

/**
 * @author mtengelm
 *
 */
public class TestQueryProcessor extends TestCase {

	private QueryProcessor qp;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link br.com.auster.minerva.generator.QueryProcessor#QueryProcessor(java.lang.String)}.
	 */
	public void testQueryProcessor() {
		String sql = "select * from bck_consequence";
		this.qp = new QueryProcessor(sql);
		assertNotNull(this.qp);
	}

	/**
	 * Test method for {@link br.com.auster.minerva.generator.QueryProcessor#extractParameters()}.
	 */
	public void testExtractParameters() {
		String sql = "";
		
		sql = "select * from bck_consequence";
		QueryProcessor qp = new QueryProcessor(sql);		
		List list = qp.extractParameters();
		assertEquals(list.size(), 0);
		
		sql = "select * from bck_consequence where uid = ?";
		qp = new QueryProcessor(sql);
		list = qp.extractParameters();
		assertEquals("Should not get /? as parameter ", list.size(), 0);

		sql = "select * from bck_consequence where uid = $P{PARAM_NAME}";
		qp = new QueryProcessor(sql);
		list = qp.extractParameters();
		assertEquals("Should get 1 parameter", list.size(), 1);		
		
	}

	/**
	 * Test method for {@link br.com.auster.minerva.generator.QueryProcessor#extractJDBCQuery()}.
	 */
	public void testExtractJDBCQuery() {
		String sql = "";
		
		sql = "select * from bck_consequence";
		QueryProcessor qp = new QueryProcessor(sql);		
		String res = qp.extractJDBCQuery();
		assertEquals("Should be equal." , res, sql);		
		
		sql = "select * from bck_consequence where uid = ?";
		qp = new QueryProcessor(sql);
		res = qp.extractJDBCQuery();
		assertEquals("Sould be equal", res,sql);		
		
		String sql1 = "select * from bck_consequence where uid = ?";
		sql = "select * from bck_consequence where uid = $P{PARAM_NAME}";		
		qp = new QueryProcessor(sql);		
		res = qp.extractJDBCQuery();
		assertEquals("Sould be equal", res,qp.extractJDBCQuery());		
	}

}
