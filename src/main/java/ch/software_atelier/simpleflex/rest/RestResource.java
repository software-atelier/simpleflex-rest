package ch.software_atelier.simpleflex.rest;

import ch.software_atelier.simpleflex.rest.swagger.MethodDocumentation;

public interface RestResource {
    public RestResponse onPOST(RestRequest request);
    public RestResponse onGET(RestRequest request);
    public RestResponse onOPTIONS(RestRequest request);
    public RestResponse onPUT(RestRequest request);
    public RestResponse onDELETE(RestRequest request);
    public RestResponse onPATCH(RestRequest request);
    public RestResponse onHEAD(RestRequest request);
    
    public void docPOST(MethodDocumentation request);
    public void docGET(MethodDocumentation request);
    public void docOPTIONS(MethodDocumentation request);
    public void docPUT(MethodDocumentation request);
    public void docDELETE(MethodDocumentation request);
    public void docPATCH(MethodDocumentation request);
    public void docHEAD(MethodDocumentation request);
}
