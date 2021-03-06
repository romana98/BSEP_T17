package bsep.admin.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CertificateCreationDTO {

    @Positive
    private int subjectID;

    @NotNull
    private KeyUsageDTO keyUsageDTO;

    @NotNull
    private ExtendedKeyUsageDTO extendedKeyUsageDTO;

    public CertificateCreationDTO() {

    }

    public CertificateCreationDTO(int subjectID, KeyUsageDTO keyUsageDTO, ExtendedKeyUsageDTO extendedKeyUsageDTO) {
        this.subjectID = subjectID;
        this.keyUsageDTO = keyUsageDTO;
        this.extendedKeyUsageDTO = extendedKeyUsageDTO;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public KeyUsageDTO getKeyUsageDTO() {
        return keyUsageDTO;
    }

    public void setKeyUsageDTO(KeyUsageDTO keyUsageDTO) {
        this.keyUsageDTO = keyUsageDTO;
    }

    public ExtendedKeyUsageDTO getExtendedKeyUsageDTO() {
        return extendedKeyUsageDTO;
    }

    public void setExtendedKeyUsageDTO(ExtendedKeyUsageDTO extendedKeyUsageDTO) {
        this.extendedKeyUsageDTO = extendedKeyUsageDTO;
    }
}
