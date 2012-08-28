
package com.polk.vindecoder;



/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.4-b01-
 * Generated source version: 2.1
 * 
 */
public class VinDecoderException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private VindecoderFaultBean faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public VinDecoderException(String message, VindecoderFaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public VinDecoderException(String message, VindecoderFaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.polk.vindecoder.webservice.clientgen.VindecoderFaultBean
     */
    public VindecoderFaultBean getFaultInfo() {
        return faultInfo;
    }

}