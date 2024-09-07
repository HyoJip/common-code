package me.bbbicb.commoncode.enums;

import static java.util.Arrays.stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.lang.String;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bbbicb.commoncode.core.CommonCode;

@RequiredArgsConstructor
@Getter
public enum SettlementType implements CommonCode {
  AUTO("AUTO", "자동", 1),

  MANUAL("MANUAL", "수동", 2);

  private final String codeKey;

  private final String description;

  private final int order;

  @JsonCreator
  public static SettlementType fromValue(String value) {
    return stream(SettlementType.values())
        .filter(s -> s.codeKey.equals(value))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
  }
}
