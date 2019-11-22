package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class FileUploadParameter extends Parameter{
    public FileUploadParameter(String name, String description, boolean required){
        super(name);
        setDescription(description);
        setRequired(required);
    }
    
    public JSONObject toJSON(){
        JSONObject result = super.toJSON();
        result.put("in","formData");
        result.put("type","file");
        return result;
    }
}
