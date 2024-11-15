package mcnc.survwey.domain.survey.inquiry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyInfoDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/inquiry")
@Tag(name = "설문 조회", description = "응답/생성한 설문 리스트 조회, 상세 조회 및 검색 API")
public class SurveyInquiryController {

    private final SurveyInquiryService surveyInquiryService;

    /**
     * 사용자가 생성한 설문 목록 조회
     *
     * @param
     * @return
     */
    @GetMapping("/created")
    @Operation(summary = "사용자 본인이 생성한 설문 리스트 조회", description = "쿼리 파라미터 형식으로 size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 생성한 설문 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyWithCountDTO>> inquiryUserCreatedSurveyList(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithCountDTO> userCreatedSurveyList = surveyInquiryService.getUserCreatedSurveyList(userId, page, size);
        return ResponseEntity.ok(userCreatedSurveyList);
    }

    /**
     * 사용자가 응답한 설문 목록 조회
     *
     * @param
     * @return
     */
    @GetMapping("/respond")
    @Operation(summary = "사용자 본인이 응답한 설문 리스트 조회", description = "쿼리 파라미터 형식으로 size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 응답한 설문 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyDTO>> inquiryUserRespondSurveyList(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyDTO> userRespondSurveyList = surveyInquiryService.getUserRespondSurveyList(userId, page, size);
        return ResponseEntity.ok(userRespondSurveyList);
    }

    /**
     * 설문 응답을 위한 설문, 질문, 보기 조회
     *
     * @param surveyId
     * @return
     */
    @GetMapping("/detail/{surveyId}")
    @Operation(summary = "특정 설문/질문/보기 조회", description = "surveyId(설문 아이디)로 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "특정 설문 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<SurveyWithDetailDTO> inquirySurveyWithDetail(@PathVariable("surveyId") Long surveyId) {
        SurveyWithDetailDTO surveyWithDetailDTO = surveyInquiryService.getSurveyWithDetail(surveyId);
        return ResponseEntity.ok(surveyWithDetailDTO);
    }

    /**
     * 사용자가 특정 키워드로 설문을 찾기 위해 설문 조회
     * @param title
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "사용자 본인이 생성한 설문 검색", description = "쿼리 파라미터 형식으로 title(검색할 키워드), size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성한 설문 검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> surveySearch(@RequestParam String title,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();

        Page<Survey> surveys = surveyInquiryService.surveySearch(userId, title, page, size);
        Page<SurveyInfoDTO> surveyDTOs = surveys.map(SurveyInfoDTO::of);
        return ResponseEntity.ok(surveyDTOs);
    }

    /**
     * 사용자가 특정 키워드로 참여한 설문 검색
     * @param title
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search/respond")
    @Operation(summary = "사용자 본인이 참여한 설문 검색", description = "쿼리 파라미터 형식으로 title(검색할 키워드), size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여한 설문 검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> respondedSurveySearch(@RequestParam String title,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();

        Page<Survey> surveys = surveyInquiryService.respondedSurveySearch(userId, title, page, size);
        Page<SurveyInfoDTO> surveyInfoDTOS = surveys.map(SurveyInfoDTO::of);
        return ResponseEntity.ok(surveyInfoDTOS);

    }
}
