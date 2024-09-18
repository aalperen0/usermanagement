import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserDTO, UserService } from '../services/user.service';
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'app-update-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Update User</h2>
    <form (ngSubmit)="onSubmit()" *ngIf="user">
      <div>
        <label for="name">Name:</label>
        <input type="text" id="name" [(ngModel)]="user.name" name="name" required (ngModelChange)="fieldChanged('name')">
      </div>
      <div>
        <label for="surname">Surname:</label>
        <input type="text" id="surname" [(ngModel)]="user.lastName" name="surname" required (ngModelChange)="fieldChanged('lastName')">
      </div>
      <div>
        <label for="email">Email:</label>
        <input type="email" id="email" [(ngModel)]="user.email" name="email" required (ngModelChange)="fieldChanged('email')">
      </div>
      <div>
        <label for="password">Password:</label>
        <input type="password" id="password" [(ngModel)]="user.password" name="password" required (ngModelChange)="fieldChanged('password')">
      </div>
      <button type="submit">Update User</button>
      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </form>
  `,
  styles: [`
    form {
      display: flex;
      flex-direction: column;
      max-width: 300px;
      margin: 0 auto;
    }

    div {
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
      margin-top: 10px;
      padding: 10px;
      background-color: #4CAF50;
      color: white;
      border: none;
      cursor: pointer;
    }

    button:active {
      transform: translateY(4px);
    }

    .error-message {
      margin-top: 2rem;
      color: red;
    }
  `
  ]
})
export class UpdateUserComponent implements OnInit {
  user: UserDTO | null = null;
  originalUser: UserDTO | null = null;
  changedFields: Set<keyof UserDTO> = new Set();
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userService.getUser(id).subscribe({
        next: (user) => {
          this.user = {...user};
          this.originalUser = {...user};
        },
        error: (err: HttpErrorResponse) => this.errorMessage = err.error
      });
    }
  }

  fieldChanged(field: keyof UserDTO) {
    if (this.user && this.originalUser) {
      if (this.user[field] !== this.originalUser[field]) {
        this.changedFields.add(field);
      } else {
        this.changedFields.delete(field);
      }
    }
  }

  onSubmit() {
    if (this.user) {
      const updatedFields = Array.from(this.changedFields).reduce((acc, field) => {
        if (this.user) {
          return { ...acc, [field]: this.user[field] };
        }
        return acc;
      }, {} as Partial<UserDTO>);

      this.userService.updateUser(this.user.id, updatedFields).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
        },
        error: (err: HttpErrorResponse) => this.errorMessage = err.error
      });
    }
  }
}
