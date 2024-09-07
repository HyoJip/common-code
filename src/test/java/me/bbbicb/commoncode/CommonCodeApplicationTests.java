package me.bbbicb.commoncode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import me.bbbicb.commoncode.enums.AdStatus;
import me.bbbicb.commoncode.enums.SettlementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommonCodeApplicationTests {

  ObjectMapper om = new ObjectMapper();

  @Test
  @DisplayName("공통코드 직렬화 시, key-value으로 직렬화된다.")
  void enum_marshalling() throws JsonProcessingException {

    TestDTO testDTO = new TestDTO();
    testDTO.foo = "foo";
    testDTO.status = AdStatus.READY;
    testDTO.settlementType = SettlementType.MANUAL;
    String serialized = om.writeValueAsString(testDTO);
    System.out.println(serialized);
  }

  @Getter
  @Setter
  static class TestDTO {

    private String foo;
    private AdStatus status;
    private SettlementType settlementType;
  }

}
