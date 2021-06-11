package bsep.hospital.api;

import bsep.hospital.dto.CertificateRequestDTO;
import bsep.hospital.service.CertificateRequestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@CrossOrigin(origins = "https://localhost:4205")
@RestController
@RequestMapping(value = "/certificate-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateRequestController {

    @Autowired
    CertificateRequestService certificateRequestService;
    private static Logger logger = LogManager.getLogger(CertificateRequestController.class);

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createAndSendCertificateRequest(@RequestBody @Valid CertificateRequestDTO certificateRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KeycloakPrincipal kcp = (KeycloakPrincipal) authentication.getPrincipal();

        KeycloakSecurityContext session = kcp.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        logger.info("User with the email " + accessToken.getEmail() + " is trying to send a certificate request.");

        boolean success = certificateRequestService.sendCertificateRequest(certificateRequestDTO);
        if (success) {
            logger.info("User with the email " + accessToken.getEmail() + " successfully sent a certificate request.");
            return new ResponseEntity<>(true,HttpStatus.OK);
        } else {
            logger.warn("User with the email " + accessToken.getEmail() + " failed to send a certificate request.");
            return new ResponseEntity<>(false,HttpStatus.BAD_REQUEST);
        }
    }
}
