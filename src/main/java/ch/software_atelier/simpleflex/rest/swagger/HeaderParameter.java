package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class HeaderParameter extends Parameter{
    
    public HeaderParameter(String name, String description){
        super(name);
        setDescription(description);
        setRequired(true);
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject result = super.toJSON();
        result.put("in","header");
        return result;
    }
}
