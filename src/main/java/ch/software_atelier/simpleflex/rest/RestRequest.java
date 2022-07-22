package ch.software_atelier.simpleflex.rest;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.software_atelier.simpleflex.*;
import org.json.JSONArray;
import org.json.JSONObject;

import xmlwise.XmlElement;

public class RestRequest {
    
    private Request _request;
    private String _appName;
    private String _resourcePattern;
    
    public RestRequest(Request r, String appName, String resourcePattern){
        _request = r;
        _resourcePattern = resourcePattern;
        _appName = appName;
    }
    
    public String getPath(){
        if (_appName.length() == 0)
            return _request.getReqestString();
        else{
            return _request.getReqestString().substring(_appName.length()+1);
        }
    }
    
    public String getResourcePlaceholder(String name){
        List<String> resurcePath = RestHlp.getResourceNodes(_request, _appName);
        List<String> patternPath = Utils.tokenize(_resourcePattern, "/");
        for (int i=0;i<patternPath.size();i++){
            String patternNode = patternPath.get(i);
            if (patternNode.equals("{"+name+"}"))
                    return resurcePath.get(i);
            else if (patternNode.equals("{"+name+"*}")){
                    String path = "";
                    for (int ii=i;ii<resurcePath.size();ii++){
                        path = path+"/"+resurcePath.get(ii);
                    }
                    return path;
            }
        }
        return null;
    }
    
    public String getRequestArgument(String name){
        return _request.getArgument(name);
    }
    
    public HashMap<String,String> getheaders(){
        return _request.getHeaders();
    }
    
    public String getHeaderValue(String key){
        return _request.getHeaderValue(key);
    }
    
    public boolean isXML(){
        return _request.isXMLReq();
        
    }
    
    public boolean isJSON(){
        return _request.isJSONReq();
    }
    
    public boolean isJSONArray(){
        return _request.isJSONArrReq();
    }
    
    public JSONArray getJSONArray(){
        return _request.getJSONArrReq();
    }
    
    public JSONObject getJSON(){
        return _request.getJSONReq();
    }
    
    public XmlElement getXML(){
        return _request.getXMLReq();
    }
    
    public boolean isSinglePart(){
        return _request.isSinglePartReq();
    }

    public byte[] getRawData(){
        return _request.getRawData();
    }

    public boolean isMultiPart(){
        return _request.isFormPostReq();
    }
    
    public String getSinglePartFilename(){
        return _request.getSinglePartFilename();
    }
    
    public String getSinglePartMimeType(){
        return _request.getSinglePartMimeType();
    }
    
    public File getSinglePartFile(){
        return _request.getSinglePartFile();
    }
    
    public File getFile(String fldName){
        RecievedData[] data = _request.getRecievedData();
        for (RecievedData rd : data){
            if (rd.fieldName().equals(fldName)){
                if (rd instanceof RecievedFile){
                    return ((RecievedFile)rd).file();
                }
                else{
                    return null;
                }
            }
        }
        return null;
    }
    
    public List<String> getFieldNames(){
        ArrayList<String> fldNames = new ArrayList<>();
        RecievedData[] data = _request.getRecievedData();
        for (RecievedData rd : data){
            fldNames.add(rd.fieldName());
        }
        return fldNames;
    }
    
    public byte[] getData(String fldName){
        RecievedData[] data = _request.getRecievedData();
        for (RecievedData rd : data){
            if (rd.fieldName().equals(fldName)){
                if (rd instanceof RecievedFile){
                    return ((RecievedFile)rd).getData();
                }
                else if (rd instanceof RecievedText){
                    return ((RecievedText)rd).getURLDecodedText().getBytes();
                }
            }
        }
        return null;
    }
    
    public String getFileName(String fldName){
        RecievedData[] data = _request.getRecievedData();
        for (RecievedData rd : data){
            if (rd.fieldName().equals(fldName)){
                if (rd instanceof RecievedFile){
                    return ((RecievedFile)rd).fileName();
                }
                else{
                    return null;
                }
            }
        }
        return null;
    }
}
