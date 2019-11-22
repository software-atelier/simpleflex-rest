package ch.software_atelier.simpleflex.rest;

import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.Utils;

import java.util.List;

public class RestHlp {
    public static List<String> getResourceNodes(Request r, String appName){
        String resourcePath = r.getReqestString();
        if (appName.length() > 0){
            resourcePath = resourcePath.substring(appName.length()+2);
        }
        else{
            resourcePath = resourcePath.substring(1);
        }
        if (resourcePath.endsWith("/")){
            resourcePath = resourcePath.substring(0, resourcePath.length()-1);
        }
        return Utils.tokenize(resourcePath, "/");
    }
}
