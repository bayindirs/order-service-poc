package com.example.orderservicepoc.exception.handler;

import com.example.orderservicepoc.api.MethodArgumentNotValidResponse;
import com.example.orderservicepoc.exception.RequestArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RequestArgumentNotValidExceptionHandler {

  @ExceptionHandler(RequestArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public MethodArgumentNotValidResponse methodArgumentError(Exception exception) {
    MethodArgumentNotValidResponse dto = new MethodArgumentNotValidResponse();
    dto.setErrors(errorMap((RequestArgumentNotValidException) exception));
    return dto;
  }

  private Map<String, String> errorMap(RequestArgumentNotValidException exception) {
    HashMap<String, String> map = new HashMap<>();
    map.put(exception.getArgumentName(), exception.getErrorMessage());
    return map;
  }
}
