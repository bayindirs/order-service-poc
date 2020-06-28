package com.example.orderservicepoc.api;

import lombok.Data;

import java.util.Map;

@Data
public class MethodArgumentNotValidResponse {
  private Map<String, String> errors;
}
