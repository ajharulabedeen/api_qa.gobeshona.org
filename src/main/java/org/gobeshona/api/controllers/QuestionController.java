package org.gobeshona.api.controllers;

import org.gobeshona.api.models.Question;
import org.gobeshona.api.payload.response.MessageResponse;
import org.gobeshona.api.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        var ques = questionService.getQuestion(id);
        return new ResponseEntity<>(ques, HttpStatus.OK);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions() {
        List<Question> questions = questionService.getQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @PostMapping("/question")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question ques = questionService.createQuestion(question);
        return new ResponseEntity<>(ques, HttpStatus.CREATED);
    }

    @PutMapping("/question/{id}")
    public ResponseEntity<Question> updateQuestion(@RequestBody Question question , @PathVariable Long id) {
        Question ques = questionService.updateQuestion(question,id);
        return new ResponseEntity<>(ques, HttpStatus.OK);
    }

    @DeleteMapping("/question/{id}")
    public ResponseEntity<Optional<MessageResponse>> deleteQuestion(@PathVariable Long id) {
        var ques = questionService.deleteQuestion(id);
        return new ResponseEntity<>(ques, HttpStatus.OK);
    }
}
