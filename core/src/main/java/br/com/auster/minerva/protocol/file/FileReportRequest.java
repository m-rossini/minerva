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

import br.com.auster.minerva.spi.ReportRequestBase;

/**
 * @author framos
 * @version $Id$
 */
public class FileReportRequest extends ReportRequestBase {

    
    
	// ----------------------------
	// Instance variables
	// ----------------------------

    private String suggestedFilename;
    private String generatedFilename;
    
    
    
	// ----------------------------
	// Constructors
	// ----------------------------
 
    public FileReportRequest(String _name) {
        this(_name, null);
    }

    
    
	// ----------------------------
	// Public methods
	// ----------------------------
    
    public FileReportRequest(String _name, String _suggestedFilename) {
        super(_name);
        suggestedFilename = _suggestedFilename;
    }
    
    public String getSuggestedOutputFilename() {
        return suggestedFilename;
    }
    
    public void setOutputFilename(String _filename) {
        suggestedFilename = _filename;
    }
    
    public String getGeneratedOutputFilename() {
        return generatedFilename;
    }
    
    public void setGeneratedOutputFilename(String _filename) {
        generatedFilename = _filename;
    }
    
    /**
     * @see br.com.auster.minerva.interfaces.ReportRequestBase#equals(java.lang.Object)
     */
    public boolean equals(Object _other) {
        boolean isEqual = super.equals(_other);

        FileReportRequest other = (FileReportRequest) _other;
        if (suggestedFilename != null) {
            return isEqual && suggestedFilename.equals(other.getSuggestedOutputFilename());
        } 
        isEqual &= (other.getSuggestedOutputFilename() == null);
        if (generatedFilename != null) {
            return isEqual && generatedFilename.equals(other.getGeneratedOutputFilename());
        } 
        return isEqual && (other.getGeneratedOutputFilename() == null);
    }
    
    /**
     * @see br.com.auster.minerva.interfaces.ReportRequestBase#hashCode()
     */
    public int hashCode() {
        int result = super.hashCode();
        result = 37*result + (suggestedFilename == null ? 0 : suggestedFilename.hashCode());
        result = 37*result + (generatedFilename == null ? 0 : generatedFilename.hashCode());
        return result;
    }
}    

