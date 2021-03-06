package bsep.admin.service;

import bsep.admin.exceptions.CertificateNotFoundException;
import bsep.admin.initialization.Initialize;
import bsep.admin.keystore.KeyStoreReader;
import bsep.admin.model.CerRequestInfo;
import bsep.admin.repository.CerRequestInfoRepository;
import bsep.admin.utils.CryptingUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

@Service
public class CerRequestInfoService implements ServiceInterface<CerRequestInfo> {

    @Autowired
    CerRequestInfoRepository cerRequestInfoRepository;

    @Autowired
    CryptingUtil cryptingUtil;

    @Autowired
    EmailService emailService;

    @Autowired
    KeyStoreReader keyStoreReader;

    private static Logger logger = LogManager.getLogger(CerRequestInfoService.class);


    @Override
    public List<CerRequestInfo> findAll()
    {
        logger.info("Getting all certificate requests.");
        return cerRequestInfoRepository.findAll();
    }

    public List<CerRequestInfo> findAllVerified() {
        logger.info("Getting all verified certificate requests.");
        return cerRequestInfoRepository.findByVerifiedTrue();
    }


    @Override
    public Page<CerRequestInfo> findAll(Pageable pageable) {
        logger.info("Getting all certificate requests paged.");
        return cerRequestInfoRepository.findAll(pageable);
    }

    @Override
    public CerRequestInfo findOne(int id) {
        logger.info("Getting certificate request by id " + id);
        return cerRequestInfoRepository.findById(id).orElse(null);
    }

    @Override
    public CerRequestInfo saveOne(CerRequestInfo entity) {
        logger.info("Saving certificate request.");
        CerRequestInfo cerRequestInfo = cerRequestInfoRepository.findByUserId(entity.getUserId());
        return cerRequestInfo == null ? cerRequestInfoRepository.save(entity) : null;

    }

    @Override
    public boolean delete(int id) {
        logger.info("Deleting certificate request with id " + id);
        CerRequestInfo cerRequestInfo = findOne(id);
        if (cerRequestInfo != null) {
            logger.info("Successfully deleted certificate request with id " + id);
            cerRequestInfoRepository.delete(cerRequestInfo);
            return true;
        }
        logger.error("Failed to delete certificate request with id " + id);
        return false;

    }

    @Override
    public CerRequestInfo update(CerRequestInfo entity) {
        logger.info("Verifying certificate request with id " + entity.getId());
        entity.setVerified(true);
        return cerRequestInfoRepository.save(entity);
    }

    public boolean createCertificateRequest(byte[] encryptedCSR) throws IOException, CertificateNotFoundException {

        logger.info("Generating certificate request.");
        JcaPKCS10CertificationRequest p10Object = new JcaPKCS10CertificationRequest(encryptedCSR);
        X500Name x500Name = p10Object.getSubject();
        CerRequestInfo cerRequestInfo = generateAndSaveCertificateRequest(x500Name); // returns saved certificate request

        logger.info("Sending verification email for certificate request.");
        emailService.sendEmail(cerRequestInfo.getEmail(), generateCryptedData(cerRequestInfo.getEmail()));

        return true;
    }

    private String generateCryptedData(String email) {

        logger.info("Encrypting data for email.");
        // Public key of super administrator is needed for encrypting.
        PublicKey publicKey = keyStoreReader.readCertificate("super.admin@localhost.com").getPublicKey();
        // PrivateKey privateKey = keyStoreReader.readIssuerFromStore("super.admin@admin.com").getPrivateKey();
        byte[] encrypted = cryptingUtil.encrypt(email + "trimusketara", publicKey);
        return Hex.encodeHexString(encrypted);
        // return Arrays.toString(Hex.encode(encrypted));
        // return Base64.getEncoder().encodeToString(encrypted);
    }

    private CerRequestInfo generateAndSaveCertificateRequest(X500Name x500Name) {
        logger.info("Generating and saving certificate request.");
        CerRequestInfo cerRequestInfo = new CerRequestInfo(getRDNValue(x500Name, BCStyle.CN),
                getRDNValue(x500Name, BCStyle.SURNAME),
                getRDNValue(x500Name, BCStyle.GIVENNAME),
                getRDNValue(x500Name, BCStyle.O),
                getRDNValue(x500Name, BCStyle.OU),
                getRDNValue(x500Name, BCStyle.C),
                Integer.parseInt(getRDNValue(x500Name, BCStyle.UID)),
                getRDNValue(x500Name, BCStyle.E),
                false);
        logger.info("Successfully generated and saved certificate request.");
        return saveOne(cerRequestInfo);
    }

    private String getRDNValue(X500Name x500Name, ASN1ObjectIdentifier bcStyle) {
        return x500Name.getRDNs(bcStyle)[0].getFirst().getValue().toString();
    }


    public boolean verifyCertificateRequest(String encrypted) throws DecoderException, CertificateNotFoundException {
        logger.info("Verifying certificate request " + encrypted);
        byte[] encryptedByte = Hex.decodeHex(encrypted);
        PrivateKey privateKey = keyStoreReader.readIssuerFromStore("super.admin@localhost.com").getPrivateKey();
        byte[] decryptedByte = cryptingUtil.decrypt(encryptedByte, privateKey);
        String decrypted = new String(decryptedByte);
        String email = decrypted.substring(0, decrypted.length() - 12);
        CerRequestInfo cerRequestInfo = cerRequestInfoRepository.findByEmail(email);
        if (cerRequestInfo == null) {
            logger.error("Failed to find certificate request by email " + email);
            return false;
        } else {
            logger.info("Found certificate request. Verifying...");
            CerRequestInfo updated = update(cerRequestInfo);
            return updated != null;
        }
    }
}
