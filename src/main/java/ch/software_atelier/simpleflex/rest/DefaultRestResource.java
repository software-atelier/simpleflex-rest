package ch.software_atelier.simpleflex.rest;

import ch.software_atelier.simpleflex.rest.swagger.MethodDocumentation;
import org.json.JSONObject;

/**
 * An abstract RestResource that retorns 405 Method Not Allowed on every Method.
 *
 */
public abstract class DefaultRestResource implements RestResource{

    @Override
    public RestResponse onPOST(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }

    @Override
    public RestResponse onGET(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }

    @Override
    public RestResponse onOPTIONS(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }

    @Override
    public RestResponse onPUT(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }

    @Override
    public RestResponse onDELETE(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }

    @Override
    public RestResponse onPATCH(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }

    @Override
    public RestResponse onHEAD(RestRequest request) {
        return RestResponse.methodNotAllewed_405();
    }
    
    @Override
    public void docPOST(MethodDocumentation request){
        request.invalidate();
    }
    
    @Override
    public void docGET(MethodDocumentation request){
        request.invalidate();
    }
    
    @Override
    public void docOPTIONS(MethodDocumentation request){
        request.invalidate();
    }
    
    @Override
    public void docPUT(MethodDocumentation request){
        request.invalidate();
    }
    
    @Override
    public void docDELETE(MethodDocumentation request){
        request.invalidate();
    }
    
    @Override
    public void docPATCH(MethodDocumentation request){
        request.invalidate();
    }
    
    @Override
    public void docHEAD(MethodDocumentation request){
        request.invalidate();
    }
    
    public static void addSimplePropertie(JSONObject dst, String name, String type, String description, boolean required){
        JSONObject obj = new JSONObject();
        obj.put("description", description);
        obj.put("type", type);
        obj.put("required", required);
        dst.put(name,obj);
    }
}
