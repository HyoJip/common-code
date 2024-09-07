package me.bbbicb.commoncode.core;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public interface CommonCode {

  String getCodeKey();
  String getDescription();

  @JsonValue
  default Map<String, Object> getValue() {
    return Map.of("code", getCodeKey(), "name", getDescription());
  }
}
