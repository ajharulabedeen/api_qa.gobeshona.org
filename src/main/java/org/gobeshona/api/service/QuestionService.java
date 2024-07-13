package org.gobeshona.api.service;

import org.gobeshona.api.models.Question;
import org.gobeshona.api.payload.response.MessageResponse;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    Question getQuestion(Long id);

    List<Question> getQuestions();

    Question createQuestion(Question question);

    Question updateQuestion(Question question , Long id);

    String deleteQuestion(Long id);

}
