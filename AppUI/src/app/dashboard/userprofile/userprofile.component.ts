import {HttpErrorResponse} from "@angular/common/http";
import {Component, OnInit} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {UserDTO, UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>User Profile</h1>
    <div *ngIf="currentUser">
      <form (ngSubmit)="updateProfile()">
        <div>
          <label for="name">Name:</label>
          <input type="text" id="name" [(ngModel)]="currentUser.name" name="name" required>
        </div>
        <div>
          <label for="email">Email:</label>
          <input type="email" id="email" [(ngModel)]="currentUser.email" name="email" required>
        </div>
        <div>
          <label for="password">New Password:</label>
          <input type="password" id="password" [(ngModel)]="currentUser.password" name="password">
        </div>
        <button type="submit">Update Profile</button>
      </form>
    </div>
    <div *ngIf="!currentUser">
      Loading user profile...
    </div>
    <div *ngIf="message" [ngClass]="{'success': isSuccess, 'error': !isSuccess}">
      {{ message }}
    </div>
  `,
  styles: [`
    form {
      display: flex;
      flex-direction: column;
      gap: 10px;
      max-width: 300px;
      margin: 20px auto;
    }
    label {
      font-weight: bold;
    }
    input {
      padding: 5px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }
    button {
      padding: 10px;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    button:active {
      transform: translateY(4px);
    }
    .success {
      color: green;
    }
    .error {
      color: red;
    }
  `]
})
export class UserProfileComponent implements OnInit {
  currentUser: UserDTO | null = null;
  message: string = '';
  isSuccess: boolean = false;

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    const userEmail = this.authService.getCurrentUserEmail();
    if (userEmail) {
      this.userService.getUserByEmail(userEmail).subscribe({
        next: (user: UserDTO) => {
          this.currentUser = user;
        },
        error: (error: HttpErrorResponse) => {
          this.message = 'Failed to load user profile';
          this.isSuccess = false;
        }
      });
    }
  }

  updateProfile() {
    if (this.currentUser) {
      const updatedUser: UserDTO = {
        ...this.currentUser,
        password: this.currentUser.password
      };

      this.userService.updateUser(this.currentUser.id, updatedUser).subscribe({
        next: () => {
          this.message = 'Profile updated successfully';
          this.isSuccess = true;
        },
        error: (err: HttpErrorResponse) => {
          this.message = err.error;
          this.isSuccess = false;
        }
      });
    }
  }
}
