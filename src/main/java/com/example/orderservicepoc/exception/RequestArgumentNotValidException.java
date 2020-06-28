package com.example.orderservicepoc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestArgumentNotValidException extends RuntimeException {
  private String argumentName;
  private String errorMessage;
}
