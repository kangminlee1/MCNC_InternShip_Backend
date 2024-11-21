package mcnc.survwey.domain.survey.common.repository.queryDSL;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.respond.QRespond.*;
import static mcnc.survwey.domain.selection.QSelection.*;
import static mcnc.survwey.domain.survey.common.QSurvey.*;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    public Page<Object[]> findSurveyListWithRespondCountByUserId(String userId, Pageable pageable) {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT s.survey_id, s.title, s.description, s.create_date, s.expire_date, " +
                        "COALESCE(r.respond_count, 0) AS respond_count ")
                .append("FROM survey s ")
                .append("LEFT JOIN (SELECT survey_id, COUNT(*) AS respond_count " +
                        "   FROM respond " +
                        "   GROUP BY survey_id) r ON s.survey_id = r.survey_id ")
                .append("WHERE s.user_id = :userId ")
                .append("ORDER BY " +
                        "CASE " +
                        "   WHEN s.expire_date > NOW() THEN 0 " +
                        "   ELSE 1 " +
                        "END, " +
                        "ABS(TIMESTAMPDIFF(SECOND, NOW(), s.expire_date)) ASC");

        Query query = entityManager.createNativeQuery(sql.toString());

        query.setParameter("userId", userId);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<Object[]>(query.getResultList(), pageable, 0);
    }


    @Override
    public Page<SurveyDTO> findRespondedSurveyByUserId(String userId, Pageable pageable) {
        List<SurveyDTO> surveyDTOList = jpaQueryFactory
                .select(Projections.constructor(
                        SurveyDTO.class,
                        survey.surveyId,
                        survey.title,
                        survey.description,
                        survey.createDate,
                        survey.expireDate))
                .from(survey)
                .join(respond).on(respond.survey.eq(survey))
                .where(respond.user.userId.eq(userId))
                .orderBy(respond.respondDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(surveyDTOList, pageable, 0);
    }


    @Override
    public Survey getSurveyWithDetail(Long surveyId) {
        return jpaQueryFactory.selectDistinct(survey)
                .from(survey)
                .leftJoin(question).on(question.survey.eq(survey))
                .fetchJoin() //관련된 것 한번에 가져오기 위해
                .leftJoin(selection).on(selection.question.eq(question))
                .fetchJoin()
                .where(survey.surveyId.eq(surveyId))
                .fetchFirst();
    }

}