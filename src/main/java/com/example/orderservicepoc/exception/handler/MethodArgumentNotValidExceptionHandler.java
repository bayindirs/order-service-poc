package com.example.orderservicepoc.exception.handler;

import com.example.orderservicepoc.api.MethodArgumentNotValidResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public MethodArgumentNotValidResponse methodArgumentError(Exception exception) {
    MethodArgumentNotValidResponse dto = new MethodArgumentNotValidResponse();
    dto.setErrors(errorMap((MethodArgumentNotValidException) exception));
    return dto;
  }

  private Map<String, String> errorMap(MethodArgumentNotValidException exception) {
    HashMap<String, String> map = new HashMap<>();
    exception
            .getBindingResult()
            .getFieldErrors()
            .forEach(e -> map.put(e.getField(), e.getDefaultMessage()));
    return map;
  }
}
