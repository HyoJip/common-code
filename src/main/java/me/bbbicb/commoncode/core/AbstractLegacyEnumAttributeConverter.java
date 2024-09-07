package me.bbbicb.commoncode.core;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractLegacyEnumAttributeConverter<E extends Enum<E> & CommonCode> implements AttributeConverter<E, String> {

  private final Class<E> targetEnumClazz;
  private final boolean nullable;
  private final String enumName;

  public AbstractLegacyEnumAttributeConverter(Class<E> targetEnumClazz, String enumName) {
    this(targetEnumClazz, false, enumName);
  }

  @Override
  public String convertToDatabaseColumn(E e) {
    return CommonCodeUtils.toCode(e);
  }

  @Override
  public E convertToEntityAttribute(String s) {
    return CommonCodeUtils.ofCode(targetEnumClazz, s);
  }
}
