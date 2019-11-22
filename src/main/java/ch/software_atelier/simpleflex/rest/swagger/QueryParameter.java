package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class QueryParameter extends Parameter {
    
    private String _type = "string";
    
    public QueryParameter(String name, String description, boolean required){
        super(name);
        setDescription(description);
        setRequired(required);
    }
    
    public QueryParameter(String name){
        super(name);
    }
    
    public void setType(String type){
        _type = type;
    }
    
    public JSONObject toJSON(){
        JSONObject result = super.toJSON();
        result.put("in","query");
        result.put("type",_type);
        return result;
    }
}
