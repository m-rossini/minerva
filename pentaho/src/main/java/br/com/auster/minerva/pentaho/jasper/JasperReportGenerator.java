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
 * Created on 22/12/2005
 */
package br.com.auster.minerva.pentaho.jasper;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import org.w3c.dom.Element;

import br.com.auster.minerva.core.ReportException;

/**
 * @author framos
 * @version $Id$
 */
public interface JasperReportGenerator {

  /**
   * Configures this report generator with the given configuration
   * 
   * @param config a DOM element with the proper configuration, according
   *               to the report specification
   * @throws ReportException
   */
  public void configure(Element config) throws ReportException;
  
  /**
   * Generates the report reading data from the provided Connection and writing it to
   * the provided output stream
   *  
   * @param connection a database connection for the report database
   * @param parameters a map of report parameters
   * @param output an output stream to generate the report to
   * 
   * @throws ReportException in case any problem happens
   */
  public void generateReport(Connection connection, Map parameters, OutputStream output) 
              throws ReportException;

}
