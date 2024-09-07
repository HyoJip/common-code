package me.bbbicb.commoncode.converter;

import jakarta.persistence.Converter;
import java.lang.String;
import me.bbbicb.commoncode.core.AbstractLegacyEnumAttributeConverter;
import me.bbbicb.commoncode.enums.SettlementType;

@Converter
public class SettlementTypeConverter extends AbstractLegacyEnumAttributeConverter<SettlementType> {
  private static final String ENUM_NAME = "SettlementType";

  public SettlementTypeConverter() {
    super(SettlementType.class, ENUM_NAME);
  }
}
