package ch.software_atelier.simpleflex.rest.test;

import ch.software_atelier.simpleflex.rest.DefaultRestResource;
import ch.software_atelier.simpleflex.rest.RestException;
import ch.software_atelier.simpleflex.rest.RestRequest;
import ch.software_atelier.simpleflex.rest.RestResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class Resource extends DefaultRestResource {

    @Override
    public RestResponse onGET(RestRequest request) {
        try{
            JSONObject obj = new JSONObject();
            obj.put("name",request.getResourcePlaceholder("name"));
            RestResponse resp =  RestResponse.json_200(obj);
            resp.addHeader("hello", "world");
            return resp;
        }catch(JSONException je){
            throw RestException.internalServerError500(je.getMessage());
        }
    }
    
}
