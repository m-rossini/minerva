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
 * Created on 08/11/2005
 */

package br.com.auster.minerva.csv.report;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;

/**
 * @author framos
 * @version $Id$
 */

public class QueryProcessor {
	private static final Logger log = LogFactory.getLogger(AbstractCSVReport.class);
	
  private Pattern pattern = null;
  private Matcher matcher = null;
  private String  rawQuery = null;
  
  /**
   * Creates a new QueryProcessor object for the given raw Query
   * 
   * @param query a raw query using the $P{PARAM_NAME} syntax for parameters
   */
  public QueryProcessor(String query) {
    this.rawQuery = query;
    pattern = Pattern.compile("\\$P\\{(.*?)\\}", Pattern.CASE_INSENSITIVE);
    matcher = pattern.matcher(this.rawQuery);
  }
  
  /**
   * Extracts the list of parameter names from the query this processor is 
   * attached to.
   * 
   * @return List<String> with all parameter names in the order they appear in the class;
   */
  public List<String> extractParameters() {
    List<String> parameters = new ArrayList<String>();
    matcher.reset();
    while(matcher.find()) {
      String param = matcher.group(1);
      parameters.add(param);
    }
    log.trace("Parameter list:" + parameters);
    return parameters;
  }
  
  /**
   * Extracts the valid JDBC query, replacing all occurences of $P{.*} for "?"
   * in the raw query this processor is attached to.
   * 
   * @return
   */
  public String extractJDBCQuery() {
    matcher.reset();
    return matcher.replaceAll("?");
  }
  
  
}
