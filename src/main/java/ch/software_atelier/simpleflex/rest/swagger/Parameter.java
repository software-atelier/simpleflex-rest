package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public abstract class Parameter {
    
    private String _name;
    private String _description = "";
    private boolean _required = false;
    
    public Parameter(String name){
        _name = name;
    }
    
    public void setDescription(String desc){
        _description = desc;
    }
    
    public void setRequired(boolean required){
        _required = required;
    }
    
    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("name", _name);
        obj.put("description", _description);
        obj.put("required",_required);
        return obj;
    }
}
