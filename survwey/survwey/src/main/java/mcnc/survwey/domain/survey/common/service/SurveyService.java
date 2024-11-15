package mcnc.survwey.domain.survey.common.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;

    public Survey buildAndSaveSurvey(SurveyWithDetailDTO surveyWithDetailDTO, User creator) {
        Survey createdSurvey = surveyWithDetailDTO.toEntity(creator);
        surveyRepository.save(createdSurvey);
        return createdSurvey;
    }


    public boolean deleteSurveyById(Long surveyId) {
        if (surveyRepository.existsById(surveyId)) {
            surveyRepository.deleteById(surveyId);
            return true;
        } else {
            return false;
        }
    }

    public Survey findBySurveyId(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    public List<Survey> findByUser_UserId(String userId) {
        return surveyRepository.findByUser_UserId(userId);
    }

}
