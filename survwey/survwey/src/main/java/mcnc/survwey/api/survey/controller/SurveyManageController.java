package mcnc.survwey.api.survey.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.CreateSurveyDTO;
import mcnc.survwey.api.survey.service.SurveyManageService;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.survey.Survey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static mcnc.survwey.global.config.AuthInterceptor.LOGIN_USER;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyManageController {

    private final SurveyManageService surveyManageService;

    @PostMapping("/create")
    public ResponseEntity<Object> createSurvey(@Valid @RequestBody CreateSurveyDTO createSurveyDTO,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute(LOGIN_USER) == null) {
            return ResponseEntity.status(401).body(null);
        }
        try{
            String userId = String.valueOf(session.getAttribute(LOGIN_USER));
            Survey survey = surveyManageService.createSurveyWithDetails(createSurveyDTO, userId);
            return ResponseEntity.ok().body(survey);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
