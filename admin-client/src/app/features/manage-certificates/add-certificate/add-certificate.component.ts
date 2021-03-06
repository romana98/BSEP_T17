import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CertificateCreation, ExtendedKeyUsages, KeyUsages } from 'src/app/model/consent-request/certificate-request.model';
import { RequestCertificateService } from 'src/app/service/certificate-requests/certificate-requests.service';

@Component({
  selector: 'app-add-certificate',
  templateUrl: './add-certificate.component.html',
  styleUrls: ['./add-certificate.component.css']
})
export class AddCertificateComponent implements OnInit {

  templates: String[] = ["Verify digital signature", "Key Transport", "Key Agreement"]
  formData: FormGroup;

  constructor(private fb: FormBuilder, private requestCertificateService: RequestCertificateService,
    public dialogRef: MatDialogRef<AddCertificateComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { subjectId: number}) {
    this.formData = fb.group({
      'subjectID': this.data.subjectId,
      'cRLSign': false,
      'dataEncipherment': false,
      'decipherOnly': false,
      'digitalSignature': false,
      'encipherOnly': false,
      'keyAgreement': false,
      'keyCertSign': false,
      'keyEncipherment': false,
      'nonRepudiation': false,
      'serverAuth': false,
      'clientAuth': false,
      'codeSigning': false,
      'emailProtection': false,
      'timeStamping': false,
      'OCSPSigning': false,
    })
   }

  ngOnInit(): void {
  }

  createCertificate(){
    let keyUsageDTO: KeyUsages = {
      cRLSign: this.formData.controls['cRLSign'].value,
      dataEncipherment: this.formData.controls['dataEncipherment'].value,
      decipherOnly: this.formData.controls['decipherOnly'].value,
      digitalSignature: this.formData.controls['digitalSignature'].value,
      encipherOnly: this.formData.controls['encipherOnly'].value,
      keyAgreement: this.formData.controls['keyAgreement'].value,
      keyCertSign: this.formData.controls['keyCertSign'].value,
      keyEncipherment: this.formData.controls['keyEncipherment'].value,
      nonRepudiation:this.formData.controls['nonRepudiation'].value,
    };

    let extendedKeyUsages: ExtendedKeyUsages = {
      serverAuth: this.formData.controls['serverAuth'].value,
      clientAuth: this.formData.controls['clientAuth'].value,
      codeSigning: this.formData.controls['codeSigning'].value,
      emailProtection: this.formData.controls['emailProtection'].value,
      timeStamping: this.formData.controls['timeStamping'].value,
      OCSPSigning:this.formData.controls['OCSPSigning'].value
    }

    let certificate: CertificateCreation = {subjectID: this.data.subjectId, keyUsageDTO: keyUsageDTO, extendedKeyUsageDTO: extendedKeyUsages};

    this.requestCertificateService.acceptRequest(certificate).toPromise().then(result => {
        this.dialogRef.close(true);
    }, error =>{
        this.dialogRef.close(false);
    });
  }

  typeChanged(template: String){
    switch(template){
      case "Verify digital signature": {
          this.formData.reset();
          this.formData.controls['digitalSignature'].patchValue(true);
          this.formData.controls['nonRepudiation'].patchValue(true);
          this.formData.controls['keyCertSign'].patchValue(true);
          this.formData.controls['cRLSign'].patchValue(true);
          break;
      }
      case "Key Transport": {
        this.formData.reset();
        this.formData.controls['keyEncipherment'].patchValue(true);
        break;
      }
      case "Key Agreement": {
        this.formData.reset();
        this.formData.controls['keyAgreement'].patchValue(true);
        break;
      }
    }
  }

}
