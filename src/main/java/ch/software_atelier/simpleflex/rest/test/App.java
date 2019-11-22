package ch.software_atelier.simpleflex.rest.test;


import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.SimpleFlexBase;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.rest.RestHandler;
import ch.software_atelier.simpleflex.rest.RestResponse;

import java.util.HashMap;

/**
 *
 * @author tk
 */
public class App implements WebApp {
    
    private RestHandler _restHandler;
    
    public static void main(String[] args){
        SimpleFlexBase.serveOnLocalhost(App.class.getName(), new HashMap(), 18001);
    }
    
    @Override
    public WebDoc process(Request request) {
        if (_restHandler.handles(request)){
            return _restHandler.handle(request);
        }
        return RestResponse.notFound_404();
    }

    @Override
    public void start(String name, HashMap<String,Object> config, SimpleFlexAccesser sfa) {
        _restHandler = new RestHandler(name);
        _restHandler.registerResource("/name/{name}", new Resource());
        _restHandler.registerResource("/path/{path*}", new AsteriskResource());
    }


    @Override
    public long maxPostingSize(String requestedPath) {
        return 1024;
    }

    @Override
    public void quit() {
    }
    
}
