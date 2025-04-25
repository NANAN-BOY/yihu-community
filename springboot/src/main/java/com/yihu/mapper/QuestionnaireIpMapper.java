package com.yihu.mapper;

import com.yihu.entity.QuestionnaireIp;
import io.lettuce.core.dynamic.annotation.Param;

public interface QuestionnaireIpMapper {

    Integer isSubmit(@Param("questionnaireId") Integer questionnaireId, @Param("ip") String ip);

    void insertQuestionnaireIp(QuestionnaireIp questionnaireIp);
}
