package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class BodyParameter extends Parameter {
    private JSONObject _schema = new JSONObject();
        
    public BodyParameter(String name, JSONObject schema){
        super(name);
        setSchema(schema);
    }
    
    public void setSchema(JSONObject schema){
        _schema = schema;
    }
    
    public JSONObject toJSON(){
        JSONObject result = super.toJSON();
        result.put("in","body");
        result.put("schema",_schema);
        return result;
    }
}
