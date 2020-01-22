package com.ryan.temporarycyclechange.exception;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 
 * @author rsapl00
 */
@ControllerAdvice
@RestController
public class HostPostResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

        @ResponseBody
        @ExceptionHandler({ Exception.class, HostPosDatabaseEntryCorruptedException.class, MailSendException.class,
                        ReportGenerationException.class })
        public final ResponseEntity<Object> handleHostPosException(Exception ex, WebRequest request) throws Exception {

                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(ex.getMessage()), request.getDescription(false));

                return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ResponseBody
        @ExceptionHandler(AccessDeniedException.class)
        public final ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request)
                        throws Exception {

                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(ex.getMessage()), request.getDescription(false));

                return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
        }

        @ResponseBody
        @ExceptionHandler({ ChronologicalDateException.class, CycleChangeRequestUpdateException.class,
                        BufferDaysException.class })
        public final ResponseEntity<Object> handleChronologicalDateException(Exception ex, WebRequest request)
                        throws Exception {

                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(ex.getMessage()), request.getDescription(false));

                return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }

        @ResponseBody
        @ExceptionHandler({ MaximumRunSchedulePerRunDateException.class, CycleChangeRequestOffsiteException.class,
                        InvalidEffectiveDate.class, CycleChangeRequestApprovalException.class,
                        CycleChangeRequestCancelException.class, RunSequenceException.class })
        public final ResponseEntity<Object> handleUnprocessableEntityExceptions(Exception ex, WebRequest request) {

                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(ex.getMessage()), request.getDescription(false));

                return new ResponseEntity<>(exceptionResponse, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        @ResponseBody
        @ExceptionHandler({ CycleChangeNotFoundException.class })
        public final ResponseEntity<Object> handleCycleChangeNotFoundException(Exception ex, WebRequest request) {
                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(ex.getMessage()), request.getDescription(false));

                return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
        }

        /**
         * Handles all Constraint validation exceptions for RequestBody parameters in
         * Controllers.
         * 
         * Example: @RequestBody CycleChangeRequest
         */
        @ResponseBody
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                        HttpHeaders headers, HttpStatus status, WebRequest request) {

                List<String> errors = ex.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage())
                                .collect(Collectors.toList());

                HostPosExceptionResource exceptionResource = new HostPosExceptionResource(new Date(), errors,
                                String.valueOf(ex.getBindingResult().getAllErrors().get(0)));

                return new ResponseEntity<>(exceptionResource, status);
        }

        @ResponseBody
        @Override
        protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                        HttpStatus status, WebRequest request) {

                String errorMessage = "Invalid value: " + ex.getValue()
                                + ". For date, value should be in yyyy-MM-dd format.";

                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(errorMessage), ex.getMessage());

                return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }

        @ResponseBody
        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                        HttpHeaders headers, HttpStatus status, WebRequest request) {

                String errorMessage = "Invalid request. Check your request body for any missing values.";

                HostPosExceptionResource exceptionResponse = new HostPosExceptionResource(new Date(),
                                Arrays.asList(errorMessage), ex.getMessage());

                return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }

}