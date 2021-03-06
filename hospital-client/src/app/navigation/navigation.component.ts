import {Component, OnInit} from '@angular/core';
import {StorageService} from '../services/storage/storage.service';
import {LoginService} from '../services/login/login.service';
import {Router} from '@angular/router';
import {UserRole} from '../model/log-in.model';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  role: UserRole;
  adminRole: UserRole = UserRole.ADMIN;
  doctorRole: UserRole = UserRole.DOCTOR;
  unauthorizedRole: UserRole = UserRole.UNAUTHORIZED;


  constructor(private storageService: StorageService,
              private loginService: LoginService,
              public router: Router) {
  }

  ngOnInit(): void {
    this.storageService.watchStorage().subscribe(() => {
      const user = JSON.parse(sessionStorage.getItem('user'));
      if (user === null) {
        this.role = UserRole.UNAUTHORIZED;
        this.router.navigate(['/']);
      } else {
        this.role = user.role === 'ADMIN' ? UserRole.ADMIN : UserRole.DOCTOR;
      }
    });

    const user = JSON.parse(sessionStorage.getItem('user'));
    if (user === null) {
      this.role = UserRole.UNAUTHORIZED;
    } else {
      this.role = user.role === 'ADMIN' ? UserRole.ADMIN : UserRole.DOCTOR;
    }
  }

  signOut($event: any): void {
    window.location.href = 'https://localhost:8443/auth/realms/hospital-portal/protocol/openid-connect/logout?redirect_uri=https://localhost:4205';
  }

}
