package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONArray;
import org.json.JSONObject;

public class MethodDocumentation {
    
    private JSONObject _doc = new JSONObject();
    
    public MethodDocumentation (){
        _doc.put("produces", new JSONArray());
        _doc.put("parameters", new JSONArray());
        _doc.put("responses", new JSONObject());
        _doc.put("description","");
        _doc.put("summary", "???");
        _doc.put("tags",new JSONArray());
    }
    
    public void addProduces(String mime){
        _doc.getJSONArray("produces").put(mime);
    }
    
    public void addParameter(Parameter parameter){
        if (parameter instanceof BodyParameter){
            JSONObject requestBodyObject = new JSONObject();
            requestBodyObject.put("description", parameter.toJSON().getString("description"));
            requestBodyObject.put("required", true);
            JSONObject content = new JSONObject();
            requestBodyObject.put("content", content);
            JSONObject description = new JSONObject();
            content.put("application/json", description);
            description.put("schema",parameter.toJSON().getJSONObject("schema"));
            _doc.put("requestBody", requestBodyObject);
        }
        else if (parameter instanceof FileUploadParameter){
            _doc.getJSONArray("parameters").put(parameter.toJSON());
            _doc.put("consumes", new JSONArray());
            _doc.getJSONArray("consumes").put("multipart/form-data");
        }
        else
            _doc.getJSONArray("parameters").put(parameter.toJSON());
    }
    
    public void setDescription(String desc){
        _doc.put("description",desc);
    }
    
    public void setTitle(String title){
        _doc.put("summary",title);
    }
    
    public void addTag(String tag){
        _doc.getJSONArray("tags").put(tag);
    }
    
    public void addResponse(String code, String description, JSONObject schema){
        JSONObject codeObject = new JSONObject();
        codeObject.put("description", description);
        if (!schema.keySet().isEmpty())
            codeObject.put("schema", schema);
        _doc.getJSONObject("responses").put(code,codeObject);
    }
    
    
    public JSONObject toJSON(){
        return _doc;
    }
    
    public void invalidate(){
        _doc = null;
    }
    
    public boolean isValid(){
        return _doc != null;
    }
    
}
