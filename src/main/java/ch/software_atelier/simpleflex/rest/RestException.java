package ch.software_atelier.simpleflex.rest;

public class RestException extends RuntimeException {
    
    private int _errorCode;
    private String _errorMsg;
    private Object _bodyMsg;
    
    public RestException(int errorCode, String errorMsg, Object bodyMsg){
        _errorMsg = errorMsg;
        _errorCode = errorCode;
        _bodyMsg = bodyMsg;
    }

    public int getErrorCode() {
        return _errorCode;
    }

    public String getErrorMsg() {
        return _errorMsg;
    }

    public Object getBodyMsg() {
        return _bodyMsg;
    }
    
    public static RestException internalServerError500(String msg){
        return new RestException(500, "Internal Server Error", msg);
    }
}
