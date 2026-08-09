package com.chukchuk.haksa.domain.student.model;

import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** 척척학사의 성적 type 데이터 형식을 변환한다. */
@Converter(autoApply = false)
public class GradeTypeConverter implements AttributeConverter<GradeType, String> {

  @Override
  public String convertToDatabaseColumn(GradeType attribute) {
    return attribute != null ? attribute.getValue() : null;
  }

  @Override
  public GradeType convertToEntityAttribute(String dbData) {
    for (GradeType type : GradeType.values()) {
      if (type.getValue().equals(dbData)) {
        return type;
      }
    }
    throw new CommonException(ErrorCode.INVALID_GRADE_TYPE);
  }
}
