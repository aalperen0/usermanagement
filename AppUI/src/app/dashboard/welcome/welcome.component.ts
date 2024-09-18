import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  template: `
    <div class="welcome-container">
      <h2>Welcome</h2>
      <div class="button-container">
        <button (click)="navigateToLoginForm()">Login</button>
        <button (click)="navigateToRegisterForm()">Register</button>
      </div>
    </div>
  `,
  styles: [`
    .welcome-container {
      max-width: 400px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 5px;
    }

    form div {
      margin-bottom: 10px;
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
  `]
})
export class WelcomeComponent {

  constructor(
    private router: Router) {
  }

  navigateToRegisterForm() {
    this.router.navigate(["/register"]);
  }

  navigateToLoginForm() {
    this.router.navigate(["/login"]);
  }


}
