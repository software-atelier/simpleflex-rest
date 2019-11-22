package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class PathParameter extends Parameter {
    
    private String _type = "string";
    
    public PathParameter(String name, String description){
        super(name);
        setRequired(true);
        setType("string");
        setDescription(description);
    }
    
    public void setType(String type){
        _type = type;
    }
    
    public JSONObject toJSON(){
        JSONObject result = super.toJSON();
        result.put("in", "path");
        result.put("type", _type);
        return result;
    }
    
}
