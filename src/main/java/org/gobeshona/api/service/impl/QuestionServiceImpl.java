package org.gobeshona.api.service.impl;


import org.gobeshona.api.models.Question;
import org.gobeshona.api.payload.response.MessageResponse;
import org.gobeshona.api.repository.QuestionRepository;
import org.gobeshona.api.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
public Question getQuestion(Long id) {

            return questionRepository.findById(id).orElseThrow(()-> new RuntimeException("Question not found with id " + id));

    }

    @Override
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question createQuestion(Question question) {
        question.setCreatedAt(LocalDateTime.now());
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Question question, Long id) {
        return null;
    }

    @Override
    public String deleteQuestion(Long id) {
        Question question = questionRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("Question not found with id " + id));



            questionRepository.delete(question);


         return "Question with id " + id + " deleted successfully";

    }
}
