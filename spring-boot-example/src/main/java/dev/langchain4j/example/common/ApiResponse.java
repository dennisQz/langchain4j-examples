package dev.langchain4j.example.common;

public class ApiResponse<T> {
    private int code;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data);
    }

    public static <T> ApiResponse<T> error(int code, T data) {
        return new ApiResponse<>(code, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
