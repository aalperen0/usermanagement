import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {CommonModule} from '@angular/common';
import {HttpClientModule, HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  providers: [AuthService],
  template: `
    <div class="register-container">
      <h2>Login</h2>
      <form (ngSubmit)="onRegister()">
        <div>
          <label for="name">Name:</label>
          <input type="text" id="name" [(ngModel)]="firstName" name="name" required>
        </div>
        <div>
          <label for="surname">Surname:</label>
          <input type="text" id="surname" [(ngModel)]="lastName" name="surname" required>
        </div>
        <div>
          <label for="email">Email:</label>
          <input type="email" id="email" [(ngModel)]="email" name="email" required>
        </div>
        <div>
          <label for="password">Password:</label>
          <input type="password" id="password" [(ngModel)]="password" name="password" required>
        </div>
        <button type="submit">Register</button>
        <div *ngIf="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
      </form>
    </div>
  `,
  styles: [`
    .register-container {
      max-width: 300px;
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
      width: 100%;
      padding: 5px;
    }

    button {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 50%;
      padding: 10px;
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
export class RegisterComponent {

  firstName: string = '';
  lastName: string = '';
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {
  }

  onRegister(): void {
    this.authService.register(this.firstName, this.lastName, this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error;
      }
    });
  }
}
