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
package br.com.auster.minerva.spi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.auster.minerva.spi.ReportRequest;


/**
 * @author framos
 * @version $Id$
 */
public abstract class ReportRequestBase implements ReportRequest, Serializable {


	
	// ----------------------------
	// Instance variables
	// ----------------------------
    
    private String name;
    private Map attributes;
    private String transactionId;
    private long timeInSecs;

    
    
	// ----------------------------
	// Constructors
	// ----------------------------

    public ReportRequestBase() {
    	this(null);
    }
    
    public ReportRequestBase(String _name) {
        name = _name;
        this.attributes = new HashMap();
    }
    

    
	// ----------------------------
	// Interface methods
	// ----------------------------

    /**
     * @see br.com.auster.minerva.spi.ReportRequest#setName()
     */
    public void setName(String _name) {
        name = _name;
    }
    
    /**
     * @see br.com.auster.minerva.spi.ReportRequest#getName()
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see br.com.auster.minerva.spi.ReportRequest#getTransactionId()
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @see br.com.auster.minerva.spi.ReportRequest#getAttributes()
     */
    public Map getAttributes() {
        return attributes;
    }
  
    /**
     * @see br.com.auster.minerva.spi.ReportRequest#getGenerationTime()
     */
    public long getGenerationTime() {
        return timeInSecs;
    }
    

    
	// ----------------------------
	// Public methods
	// ----------------------------
    
    public void setTransactionId(String _id) {
        transactionId = _id;
    }
    
    public void setAttributes(Map _map) {
        attributes = _map;
    }

    public Object addAttribute(String _key, Object _value) {
        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes.put(_key, _value);
    }
    
    public void setGenerationTime(long _timeInSecs) {
        timeInSecs = _timeInSecs;
    }    
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object _other) {
        ReportRequest other = (ReportRequest) _other;
        boolean isEqual = name.equals(other.getName());
        if (transactionId != null) {
            isEqual &= transactionId.equals(other.getTransactionId()); 
        } else {
            isEqual &= (other.getTransactionId() == null);
        }
        if (attributes != null) {
            isEqual &= attributes.equals(other.getAttributes());
        } else {
            isEqual &= (other.getAttributes() == null);
        }
        return isEqual && (timeInSecs == other.getGenerationTime());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = 17;
        result = 37*result + name.hashCode();
        result = 37*result + (transactionId == null ? 0 : transactionId.hashCode());
        result = 37*result + (attributes == null ? 0 : attributes.hashCode());
        result = 37*result + (int) timeInSecs;
        return result;
    }
}
