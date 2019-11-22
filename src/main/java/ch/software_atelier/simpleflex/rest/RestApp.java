package ch.software_atelier.simpleflex.rest;

import ch.software_atelier.simpleflex.HTTPCodes;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.JSONDoc;
import ch.software_atelier.simpleflex.docs.impl.RedirectorDoc;
import ch.software_atelier.simpleflex.docs.impl.RessourceDoc;
import ch.software_atelier.simpleflex.rest.swagger.MethodDocumentation;
import ch.software_atelier.simpleflex.rest.swagger.PathDocumentation;

import java.util.HashMap;

import org.json.JSONObject;

public abstract class RestApp implements WebApp {
    
    
    private RestHandler _handler;
    
    private JSONObject _swaggerDoc = new JSONObject();
    
    @Override
    public WebDoc process(Request request) {
        if (_handler.handles(request)){
            return _handler.handle(request);
        }
        else if (request.getReqestString().endsWith("api-doc/swagger.json")){
            return JSONDoc.json(_swaggerDoc);
        }
        else if (request.getReqestString().endsWith("api-doc") ){
            return new RedirectorDoc("api-doc/");
        }
        else if (request.getReqestString().endsWith("api-doc/")){
            return new RessourceDoc("swagger/redoc.html");
        }
        else
            return RestResponse.error(400, HTTPCodes.getMsg(400),"invalid path");
    }

    @Override
    public void start(String name, HashMap<String,Object> config, SimpleFlexAccesser sfa) {
        
        _swaggerDoc.put("swagger","2.0");
        _swaggerDoc.put("paths", new JSONObject());
        _handler = new RestHandler(name);
    }
    
    public void setDocInfo(String version, String title, String description){
        JSONObject info = new JSONObject();
        info.put("version",version);
        info.put("title", title);
        info.put("description", description);
        _swaggerDoc.put("info", info);
    }
    
    public void setDocPath(String host, String basePath){
        _swaggerDoc.put("host", host);
        _swaggerDoc.put("basePath", basePath);
    }
    
    public void addResource(String path, RestResource res){
        _handler.registerResource(path, res);
        registerSwagger(path,res);
    }
    

    @Override
    public long maxPostingSize(String requestedPath) {
        return WebApp.UNLIMITED_UPLOAD;
    }

    @Override
    public void quit() {

    }
    
    private void registerSwagger(String path, RestResource res){
        MethodDocumentation get = new MethodDocumentation();
        MethodDocumentation put = new MethodDocumentation();
        MethodDocumentation post = new MethodDocumentation();
        MethodDocumentation delete = new MethodDocumentation();
        MethodDocumentation options = new MethodDocumentation();
        MethodDocumentation head = new MethodDocumentation();
        MethodDocumentation patch = new MethodDocumentation();
        
        res.docGET(get);
        res.docPUT(put);
        res.docPOST(post);
        res.docDELETE(delete);
        res.docOPTIONS(options);
        res.docHEAD(head);
        res.docPATCH(patch);
        
        PathDocumentation pathDoc = new PathDocumentation();
        pathDoc.addMethodDocumentationIfValid("get",get);
        pathDoc.addMethodDocumentationIfValid("put",put);
        pathDoc.addMethodDocumentationIfValid("post",post);
        pathDoc.addMethodDocumentationIfValid("delete",delete);
        pathDoc.addMethodDocumentationIfValid("options",options);
        pathDoc.addMethodDocumentationIfValid("head",head);
        pathDoc.addMethodDocumentationIfValid("patch",patch);
        
        if (pathDoc.hasMethodDocumentations())
            _swaggerDoc.getJSONObject("paths").put(path, pathDoc.toJSON());
    }
    
}
