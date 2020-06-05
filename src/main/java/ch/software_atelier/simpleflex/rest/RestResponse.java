package ch.software_atelier.simpleflex.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ch.software_atelier.simpleflex.HTTPCodes;
import ch.software_atelier.simpleflex.Utils;
import ch.software_atelier.simpleflex.docs.HeaderField;
import ch.software_atelier.simpleflex.docs.WebDoc;
import org.json.JSONArray;
import org.json.JSONObject;

public class RestResponse extends WebDoc {
    private int _httpCode = 200;
    private String _httpMsg = "OK";
    
    private String _name;
    private String _mimeType;
    
    private byte[] _content = null;
    
    private InputStream _streamContent = null;
    private File _tmpFile = null;
    private long _streamLength;
    
    public static RestResponse json_200(JSONObject obj){
        return new RestResponse("data.json","application/json",obj.toString().getBytes());
    }
    
    public static RestResponse json_200(JSONArray obj){
        return new RestResponse("data.json","application/json",obj.toString().getBytes());
    }
    
    public static RestResponse json_201_created(JSONObject obj){
        RestResponse response = new RestResponse("data.json","application/json",obj.toString().getBytes());
        response.setHTTPCode(201, "Created");
        return response;
    }
    
    public static RestResponse json_201_created(JSONArray obj){
        RestResponse response = new RestResponse("data.json","application/json",obj.toString().getBytes());
        response.setHTTPCode(201, "Created");
        return response;
    }
    
    public static RestResponse notFound_404(){
        return error(404,"File Not Found","This resource was not found on this server");
    }
    
    public static RestResponse methodNotAllewed_405(){
        return error(405,"Method Not Allowed","This method is not allowed on this resource");
    }

    public static RestResponse badRequest_400(Object msg){
        return error(400,"Bad Request",msg);
    }
    
    public static RestResponse unauthorized_401(){
        return error(401,"Unauthorized","Authorisation failed");
    }
    
    public static RestResponse noContent_204(){
        return error(204, HTTPCodes.getMsg(204),null);
    }
    
    public static RestResponse internalServerError_500(Object msg){
        return error(500,"Internal Server Error",msg);
    }
    
    public static RestResponse error(int errCode, String errMsg, Object msg){
        String payload;
        if (msg == null){
            payload = "";
        }
        else if (msg instanceof JSONObject){
            payload = "{\"msg\":"+msg.toString()+"}";
        }
        else{
            payload = "{\"msg\":\""+msg+"\"}";
        }
        RestResponse response = new RestResponse("err.htm", Utils.getMimeFromFilePath("file.json"),
                payload.getBytes());
        response.setHTTPCode(errCode, errMsg);
        
        return response;
    }
    
    
    
    public RestResponse(String name, String mime, byte[] content){
        _name = name;
        _mimeType = mime;
        _content = content;
    }
    
    public RestResponse(String name, String mime, InputStream content, long size){
        _name = name;
        _mimeType = mime;
        _streamContent = content;
        _streamLength = size;
    }
    
    public RestResponse(String name, String mime, File file, boolean isTemporary)
            throws FileNotFoundException{
        _name = name;
        _mimeType = mime;
        _streamContent = new FileInputStream(file);
        _streamLength = file.length();
        if (isTemporary)
            _tmpFile = file;
    }
    
    
    public void addHeader(String name, String value){
        getHeaders().add(new HeaderField(name, value));
    }

    @Override
    public long size() {
        if (_content !=null){
            return _content.length;
        }
        else{
            return _streamLength;
        }
    }

    @Override
    public String mime() {
        return _mimeType;
    }

    @Override
    public String name() {
        return _name;
    }

    @Override
    public byte[] byteData() {
        return _content;
    }

    @Override
    public InputStream streamData() {
        return _streamContent;
    }

    @Override
    public String dataType() {
        if (_content!=null)
            return WebDoc.DATA_BYTE;
        else
            return WebDoc.DATA_STREAM;
    }

    @Override
    public void close() {
        if (_streamContent!=null){
            try{
                _streamContent.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        if (_tmpFile != null){
            if (!_tmpFile.delete()){
                _tmpFile.deleteOnExit();
            }
        }
    }
}
