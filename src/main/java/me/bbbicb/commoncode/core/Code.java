package me.bbbicb.commoncode.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Code {
  String groupCodeKey;
  String groupDescription;
  String codeKey;
  String description;
  int order;
}
