package me.bbbicb.commoncode.converter;

import jakarta.persistence.Converter;
import java.lang.String;
import me.bbbicb.commoncode.core.AbstractLegacyEnumAttributeConverter;
import me.bbbicb.commoncode.enums.AdStatus;

@Converter
public class AdStatusConverter extends AbstractLegacyEnumAttributeConverter<AdStatus> {
  private static final String ENUM_NAME = "AdStatus";

  public AdStatusConverter() {
    super(AdStatus.class, ENUM_NAME);
  }
}
