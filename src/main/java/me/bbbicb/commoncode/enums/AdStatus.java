package me.bbbicb.commoncode.enums;

import static java.util.Arrays.stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.lang.String;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bbbicb.commoncode.core.CommonCode;

@RequiredArgsConstructor
@Getter
public enum AdStatus implements CommonCode {
  READY("READY", "거래승인요청", 1),

  DOING("DOING", "거래중", 2),

  END("END", "거래마감", 3);

  private final String codeKey;

  private final String description;

  private final int order;

  @JsonCreator
  public static AdStatus fromValue(String value) {
    return stream(AdStatus.values())
        .filter(s -> s.codeKey.equals(value))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
  }
}
