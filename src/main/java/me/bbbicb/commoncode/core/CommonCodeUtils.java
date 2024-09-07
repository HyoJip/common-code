package me.bbbicb.commoncode.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonCodeUtils {

  public static final Pattern SPLIT_DELIMITER = Pattern.compile("[\\s_]");
  public static final Pattern SNAKE_DELIMITER = Pattern.compile("_[a-z]");

  public static <T extends Enum<T> & CommonCode> T ofCode(Class<T> enumInstance, String code) {
    if (!StringUtils.hasText(code)) {
      return null;
    }

    return EnumSet.allOf(enumInstance).stream()
      .filter(e -> e.getCodeKey().equals(code))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(String.format("Enum=[%s], 공통코드[%s]가 존재하지 않습니다.", enumInstance.getSimpleName(), code)));
  }

  public static <T extends Enum<T> & CommonCode> String toCode(T enumInstance) {
    if (Objects.isNull(enumInstance)) {
      return "";
    }

    return enumInstance.getCodeKey();
  }

  public static String toPascalCase(String input) {
    if (!StringUtils.hasText(input)) {
      return input;
    }

    if (input.equals(input.toUpperCase())) {
//      input = SNAKE_DELIMITER.matcher(input.toLowerCase()).replaceAll(r -> r.group().toUpperCase());
//      System.out.println("input = " + input);
      input.toLowerCase();
    }

    String[] parts = SPLIT_DELIMITER.split(input);
    StringBuilder result = new StringBuilder();

    for (String part : parts) {
      if (part.length() == 0) continue;
      result.append(Character.toUpperCase(part.charAt(0)));
      if (parts.length == 1) {
        result.append(part.substring(1));
        break;
      }
      result.append(part.substring(1).toLowerCase());
    }

    return result.toString();
  }

}
