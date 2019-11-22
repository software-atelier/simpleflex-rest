package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class Response {
    
    private JSONObject _responseData = new JSONObject();
    
    public static Response jsonResponse(JSONObject schema){
        Response response = new Response();
        response.addJSONResponse(schema);
        return response;
    }
    
    public JSONObject toJSON(){
        return _responseData;
    }
    
    public void addResponse(String mime, JSONObject schema){
        JSONObject s = new JSONObject();
        s.put("schema",schema);

        _responseData.put(mime,s);
    }
    
    public void addJSONResponse(JSONObject schema){
        _responseData.put("application/json",schema);
    }
    
    public void addTextResponse(){
        JSONObject schemaContent = new JSONObject();
        schemaContent.put("type","string");
        addResponse("text/plain", schemaContent);
    }
    
    public void addBinaryResponse(String mime){
        JSONObject schemaContent = new JSONObject();
        schemaContent.put("type","binary");
        addResponse(mime, schemaContent);
    }
    
}
