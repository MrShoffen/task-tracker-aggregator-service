package org.mrshoffen.tasktracker.aggregator.advice;


import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityAlreadyExistsException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class AggregatorControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleTaskStructureException(AccessDeniedException e) {
        ProblemDetail problem = generateProblemDetail(FORBIDDEN, e);
        return Mono.just(ResponseEntity.status(FORBIDDEN).body(problem));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleTaskNotFoundException(EntityNotFoundException e) {
        ProblemDetail problem = generateProblemDetail(NOT_FOUND, e);
        return Mono.just(ResponseEntity.status(NOT_FOUND).body(problem));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleTaskAlreadyExistsException(EntityAlreadyExistsException e) {
        ProblemDetail problem = generateProblemDetail(CONFLICT, e);
        return Mono.just(ResponseEntity.status(CONFLICT).body(problem));
    }


    private ProblemDetail generateProblemDetail(HttpStatus status, Exception ex) {
        log.warn("Error occured: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }

}
