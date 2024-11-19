package mcnc.survwey.domain.survey.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.common.Survey;


import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDTO {
    private long surveyId;
    @NotBlank(message = "설문 제목은 필수입니다.")
    private String title;
    private String description;
    private LocalDateTime createDate;
    @NotNull(message = "만료일 지정은 필수입니다.")
    private LocalDateTime expireDate;

    public static SurveyDTO of(Survey survey) {
        return SurveyDTO.builder()
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .build();
    }
}
