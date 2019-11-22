package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class ArraySchemaBuilder {
    private JSONObject _data = new JSONObject();
    
    private ArraySchemaBuilder(String description){
        _data.put("description",description);
        _data.put("type","array");
    }
    
    public static ArraySchemaBuilder create(String description){
        return new ArraySchemaBuilder(description);
    }
    
    public ArraySchemaBuilder setBasic(String type, String description){
        JSONObject schema = new JSONObject();
        schema.put("description", description);
        schema.put("type", type);
        _data.put("items",schema);
        return this;
    }
    
    public ArraySchemaBuilder setObject(JSONObject schema){
        _data.put("items",schema);
        return this;
    }
    
    public JSONObject toJSON(){
        return _data;
    }

}
