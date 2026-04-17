package com.MedicNote.authService.feign;

import com.MedicNote.authService.dto.PatientRegisterRequestDTO;
import com.MedicNote.authService.exception.DownstreamServiceException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class PatientServiceClientFallback implements FallbackFactory<PatientServiceClient> {

    @Override
    public PatientServiceClient create(Throwable cause) {
        log.error("═══════════════════════════════════════════════════");
        log.error("Patient Service fallback triggered");
        log.error("Cause type        : {}", cause.getClass().getName());
        log.error("Cause message     : {}", cause.getMessage());
        log.error("Cause.getCause()  : {}", cause.getCause() != null ? cause.getCause().getClass().getName() : "null");
        log.error("═══════════════════════════════════════════════════");

        return new PatientServiceClient() {

            @Override
            public Map<String, Object> registerPatient(PatientRegisterRequestDTO request) {
                throw buildException("Patient Service", cause);
            }

            @Override
            public Map<String, Object> loginPatient(Map<String, Object> request) {
                throw buildException("Patient Service", cause);
            }

            @Override
            public Map<String, Object> getPatientByEmail(String email) {
                throw buildException("Patient Service", cause);
            }

            @Override
            public Map<String, Object> getPatientByPhone(String phone) {
                throw buildException("Patient Service", cause);
            }
        };
    }

    private RuntimeException buildException(String serviceName, Throwable cause) {
        Throwable realCause = unwrap(cause);

        // Already a DownstreamServiceException from FeignErrorDecoder
        if (realCause instanceof DownstreamServiceException dse) {
            log.error("[{}] DownstreamServiceException — status: {}, message: {}",
                    serviceName, dse.getStatusCode(), dse.getMessage());
            return dse;
        }

        if (realCause instanceof FeignException fe) {
            String body = fe.contentUTF8();
            log.error("[{}] FeignException — status: {}, body: {}", serviceName, fe.status(), body);
            return new DownstreamServiceException(fe.status(), body.isEmpty() ? fe.getMessage() : body, fe);
        }

        if (realCause instanceof TimeoutException) {
            log.error("[{}] TimeoutException — returning 408", serviceName);
            return new DownstreamServiceException(408, serviceName + " call timed out.", realCause);
        }

        log.error("[{}] Unknown exception — returning 503: {}", serviceName, realCause.getMessage());
        return new DownstreamServiceException(503, serviceName + " is currently unavailable.", realCause);
    }

    /**
     * Unwraps Resilience4j or other wrapper exceptions to get to the real cause.
     * Resilience4j wraps FeignException inside CallNotPermittedException or similar.
     */
    private Throwable unwrap(Throwable cause) {
        Throwable current = cause;
        int maxDepth = 5; // prevent infinite loop
        while (current.getCause() != null && maxDepth-- > 0) {
            log.debug("Unwrapping: {} -> {}", current.getClass().getName(), current.getCause().getClass().getName());
            // Stop if we've already found a FeignException
            if (current instanceof FeignException) {
                break;
            }
            current = current.getCause();
        }
        return current;
    }
}