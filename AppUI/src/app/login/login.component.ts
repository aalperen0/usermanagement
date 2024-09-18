import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {CommonModule} from '@angular/common';
import {HttpClientModule, HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  providers: [AuthService],
  template: `
    <div class="login-container">
      <h2>Login</h2>
      <form (ngSubmit)="onLogin()">
        <div>
          <label for="email">Email:</label>
          <input type="email" id="email" [(ngModel)]="email" name="email" required>
        </div>
        <div>
          <label for="password">Password:</label>
          <input type="password" id="password" [(ngModel)]="password" name="password" required>
        </div>
        <div class="button-container">
          <button type="submit">Login</button>
        </div>
        <div *ngIf="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
      </form>
    </div>
  `,
  styles: [`
    .login-container {
      max-width: 400px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 5px;
    }

    form div {
      margin-bottom: 10px;
    }

    label {
      display: block;
      margin-bottom: 5px;
    }

    input {
      width: 90%;
      padding: 5px;
    }

    button-container {
      display: flex;
      justify-content: space-between;
      gap: 20px;
    }

    .button-container button {
      width: 45%;
      padding: 10px;
      margin: 20px 0 0 5px;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
    }

    button:active {
      transform: translateY(4px);
    }

    .error-message {
      color: red;
      margin-top: 10px;
    }
  `]
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService,
              private router: Router) {
  }


  /* checking user authorities when logged in
  * if its user navigate to user dashboard
  * if its admin navigate to admin dashboard
  * */
  onLogin(): void {
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        const userAuth = this.authService.checkUserRole();
        if (userAuth) {
          this.router.navigate(['/dashboard']);
        } else{
          this.router.navigate(['/user-profile'])
        }
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error
        console.error('Login error:', err.error);
      }
    });
  }


}
