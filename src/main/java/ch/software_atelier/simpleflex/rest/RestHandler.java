package ch.software_atelier.simpleflex.rest;


import java.util.ArrayList;
import java.util.List;

import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.Utils;
import ch.software_atelier.simpleflex.docs.WebDoc;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class RestHandler {
    private final String _appName;
    private final List<String> _resourcePaths = new ArrayList<>();
    private final List<RestResource> _resources = new ArrayList<>();
    static Logger LOG = LogManager.getLogger(RestHandler.class);
    
    public RestHandler(String appName){
        _appName = appName;
    }
    
    public WebDoc handle(Request r){
        try{
            int index = getPathIndex(r);
        
            RestResource resource = _resources.get(index);
            RestRequest req = new RestRequest(r, _appName, _resourcePaths.get(index));
            try{
                if (r.getMethod().equalsIgnoreCase(Request.METHOD_DELETE)){
                     return resource.onDELETE(req);
                }
                else if (r.getMethod().equalsIgnoreCase(Request.METHOD_GET)){
                    return resource.onGET(req);
                }
                else if (r.getMethod().equalsIgnoreCase(Request.METHOD_OPTIONS)){
                    return resource.onOPTIONS(req);
                }
                else if (r.getMethod().equalsIgnoreCase(Request.METHOD_PATCH)){
                    return resource.onPATCH(req);
                }
                else if (r.getMethod().equalsIgnoreCase(Request.METHOD_POST)){
                    return resource.onPOST(req);
                }
                else if (r.getMethod().equalsIgnoreCase(Request.METHOD_PUT)){
                    return resource.onPUT(req);
                }
            }catch(RestException re){
                LOG.error("",re);
                return RestResponse.error(re.getErrorCode(), re.getErrorMsg(), re.getBodyMsg());
            }
        }catch(Throwable th){
            LOG.error("",th);
        }
        return RestResponse.notFound_404();
        
    }
    
    public boolean handles(Request r){
        return getPathIndex(r) >=0;
    }
    
    private int getPathIndex(Request r){
        List<String> nodes = RestHlp.getResourceNodes(r, _appName);
        
        for (int i=0;i<_resourcePaths.size();i++){
            String path = _resourcePaths.get(i);
            List<String> templateNodes = Utils.tokenize(path, "/");
            
            
            if (nodes.size() == templateNodes.size() || templateNodes.get(templateNodes.size()-1).endsWith("*}")){ // placeholder for rest of path
                boolean handles = true;
                for (int ii=0;ii<templateNodes.size();ii++){
                    String expected = templateNodes.get(ii);
                    
                    String found = "/";
                    if (nodes.size()-1 >= ii)
                        found = nodes.get(ii);
                    if (expected.startsWith("{")&&expected.endsWith("*}"))
                        return i;
                    handles = handles && (expected.equals(found) 
                            || (expected.startsWith("{")&&expected.endsWith("}")));
                    if (!handles)
                        break;
                }
                if (handles)
                    return i;
            }
        }
        
        return -1;
    }
    
    public void registerResource(String resourcePath, RestResource resource){
        if (resourcePath.startsWith("/"))
            resourcePath = resourcePath.substring(1);
        _resourcePaths.add(resourcePath);
        _resources.add(resource);
    }
}
