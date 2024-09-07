package me.bbbicb.commoncode;


import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bbbicb.commoncode.core.AbstractLegacyEnumAttributeConverter;
import me.bbbicb.commoncode.core.Code;
import me.bbbicb.commoncode.core.CommonCode;
import me.bbbicb.commoncode.core.CommonCodeUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.javapoet.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class EnumCodeGenerator {

  public static final String BASE_PATH = "src/main/java";
  public static final String BASE_PACKAGE_PATH = "me.bbbicb.commoncode";

  public static void main(String[] args) throws Exception {
    DataSource dataSource = DataSourceBuilder.create()
      .url("jdbc:mariadb://localhost:3306/bbbicb")
      .username("root")
      .password("0000")
      .driverClassName("org.mariadb.jdbc.Driver")
      .build();

    JdbcTemplate template = new JdbcTemplate(dataSource);
    String sql = """
      SELECT a.codeKey as groupCodeKey, a.description as groupDescription, b.codeKey, b.description, b.order
        FROM group_codes a JOIN common_codes b ON a.id = b.group_code_id
      """;
    Map<String, List<Code>> codes = template.query(sql, (rs, idx) -> {
        Code code = new Code(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5));
        return code;
      }).stream()
      .collect(Collectors.groupingBy(Code::getGroupCodeKey));

    AnnotationSpec requiredArgsConstructorAnnotation = AnnotationSpec.builder(RequiredArgsConstructor.class).build();
    AnnotationSpec getterAnnotation = AnnotationSpec.builder(Getter.class).build();
    AnnotationSpec jsonCreatorAnnotation = AnnotationSpec.builder(JsonCreator.class).build();
    AnnotationSpec converterAnnotation = AnnotationSpec.builder(Converter.class).build();
    for (Map.Entry<String, List<Code>> entry : codes.entrySet()) {
      createEnumFile(entry, requiredArgsConstructorAnnotation, getterAnnotation, jsonCreatorAnnotation);
      createConverterFile(entry, converterAnnotation);
    }

  }

  private static void createEnumFile(Map.Entry<String, List<Code>> entry, AnnotationSpec requiredArgsConstructorAnnotation, AnnotationSpec getterAnnotation, AnnotationSpec jsonCreatorAnnotation) throws IOException {
    String groupCodeKey = CommonCodeUtils.toPascalCase(entry.getKey());
    System.out.println("groupCodeKey = " + groupCodeKey);

    Path path = Paths.get(BASE_PATH, BASE_PACKAGE_PATH.replace(".", "/"), "enums/" + groupCodeKey + ".java");

//    if (path.toFile().exists()) {
//      return;
//    }

    // JavaPoet Enum 클래스 생성
    TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(groupCodeKey)
      .addModifiers(Modifier.PUBLIC)
      // Annotation 생성
      .addAnnotation(requiredArgsConstructorAnnotation)
      .addAnnotation(getterAnnotation)
      // implements 생성
      .addSuperinterface(CommonCode.class)
      // Enum 필드 생성
      .addField(String.class, "codeKey", Modifier.PRIVATE, Modifier.FINAL)
      .addField(String.class, "description", Modifier.PRIVATE, Modifier.FINAL)
      .addField(int.class, "order", Modifier.PRIVATE, Modifier.FINAL)
      .addMethod(MethodSpec.methodBuilder("fromValue")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(ClassName.get(BASE_PACKAGE_PATH + ".enums", groupCodeKey))
        .addParameter(String.class, "value")
        .addStatement("return stream(" + groupCodeKey + ".values())\n"
          + ".filter(s -> s.codeKey.equals(value))\n"
          + ".findAny()\n"
          + ".orElseThrow(() -> new IllegalArgumentException(\"Unknown value: \" + value))")
        .addAnnotation(jsonCreatorAnnotation)
        .build());

    for (Code code : entry.getValue()) {
      enumBuilder.addEnumConstant(
        code.getCodeKey().toUpperCase(),
        TypeSpec.anonymousClassBuilder("$S, $S, $L", code.getCodeKey(), code.getDescription(), code.getOrder()).build()
      );
    }

    TypeSpec enumType = enumBuilder.build();
    JavaFile javaFile = JavaFile.builder(BASE_PACKAGE_PATH + ".enums", enumType)
      .addStaticImport(Arrays.class, "stream")
      .build();
    javaFile.writeTo(Paths.get(BASE_PATH));

    System.out.println("EnumCodeGenerator.createEnumFile");
  }

  private static void createConverterFile(Map.Entry<String, List<Code>> entry, AnnotationSpec converterAnnotation) throws IOException {
    String groupCodeKey = CommonCodeUtils.toPascalCase(entry.getKey());
    String filename = groupCodeKey + "Converter";

    Path path = Paths.get(BASE_PATH, BASE_PACKAGE_PATH.replace(".", "/"), "converter/" + filename + ".java");
//    if (path.toFile().exists()) {
//      return;
//    }

    ClassName abstractType = ClassName.get(AbstractLegacyEnumAttributeConverter.class);
    ClassName genericType = ClassName.get(BASE_PACKAGE_PATH + ".enums", groupCodeKey);
    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(abstractType, genericType);

    TypeSpec type = TypeSpec.classBuilder(filename)
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(converterAnnotation)
      .superclass(parameterizedTypeName)
      .addField(
        FieldSpec.builder(String.class, "ENUM_NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .initializer("$S", groupCodeKey)
          .build())
      .addMethod(MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("super($T.class, ENUM_NAME)", genericType)
        .build())
      .build();

    JavaFile javaFile = JavaFile.builder(BASE_PACKAGE_PATH + ".converter", type) .build();

    // 파일을 생성할 경로
    javaFile.writeTo(Paths.get(BASE_PATH));
    System.out.println("EnumCodeGenerator.createConverterFile");
  }
}
