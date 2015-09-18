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
package br.com.auster.minerva.protocol.file;

/**
 * @author framos
 * @version $Id$
 */
public class JasperReportRequest extends FileReportRequest {


	
	// ----------------------------
	// Instance variables
	// ----------------------------
	
    private String reportClass;
    
    

	// ----------------------------
	// Constructors
	// ----------------------------
    
    public JasperReportRequest(String _name, String _jasperClass) {
        this(_name, _jasperClass, null);
    }

    public JasperReportRequest(String _name, String _jasperClass, String _suggestedFilename) {
        super(_name, _suggestedFilename);
        reportClass = _jasperClass;
    }
    
    

	// ----------------------------
	// Public methods
	// ----------------------------
    
    public String getJasperReportClass() {
        return reportClass;
    }
    
    public void setJasperReportClass(String _jasperClass) {
        reportClass = _jasperClass;
    }
    
    /**
     * @see br.com.auster.minerva.protocol.file.FileReportRequest#equals(java.lang.Object)
     */
    public boolean equals(Object _other) {
        
        return super.equals(_other) && 
               reportClass.equals( ((JasperReportRequest)_other).getJasperReportClass());
    }
    
    /**
     * @see br.com.auster.minerva.protocol.file.FileReportRequest#hashCode()
     */
    public int hashCode() {
        int result = super.hashCode();
        result = 37*result + reportClass.hashCode();
        return result;
    }
}
