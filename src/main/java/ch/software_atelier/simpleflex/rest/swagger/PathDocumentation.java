package ch.software_atelier.simpleflex.rest.swagger;

import org.json.JSONObject;

public class PathDocumentation {
    
    public PathDocumentation(){
    }
    
    private JSONObject _doc = new JSONObject();
    
    public JSONObject toJSON(){
        return _doc;
    }
    
    public void addMethodDocumentationIfValid(String method, MethodDocumentation doc){
        if (doc.isValid())
            _doc.put(method,doc.toJSON());
    }
    
    public boolean hasMethodDocumentations(){
        return _doc.keySet().size()>0;
    }
    
}
