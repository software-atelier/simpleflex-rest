package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONArray;
import org.json.JSONObject;

public class ObjectSchemaBuilder {
    
    private JSONObject _data = new JSONObject();
    
    private ObjectSchemaBuilder(String description){
        _data.put("description",description);
        _data.put("type","object");
        _data.put("properties", new JSONObject());
        _data.put("required", new JSONArray());
    }
    
    public static ObjectSchemaBuilder create(String description){
        return new ObjectSchemaBuilder(description);
    }
    
    public ObjectSchemaBuilder addSimpleProperty(String name, String type, String description, boolean required){
        JSONObject obj = new JSONObject();
        obj.put("description", description);
        obj.put("type", type);
        _data.getJSONObject("properties").put(name,obj);
        if (required){
            _data.getJSONArray("required").put(name);
        }
        return this;
    }
    
    public ObjectSchemaBuilder addObjectProperty(String name, JSONObject object, boolean required){
        _data.getJSONObject("properties").put(name,object);
        if (required){
            _data.getJSONArray("required").put(name);
        }
        return this;
    }
    
    public JSONObject toJSON(){
        return _data;
    }
    
    
}
