import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserService, UserDTO, PagedResponse} from '../services/user.service';
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>User Management Dashboard</h1>
    <input class="searching" type="text" name="search" (ngModelChange)="onSearch($event)" [ngModel]="searchText"
           autocomplete="off" placeholder="Search a user">
    <table *ngIf="isAdmin">
      <thead>
      <tr>
        <th>Name</th>
        <th *ngIf="isDetailsVisible">Email</th>
        <th *ngIf="isDetailsVisible">Password</th>
        <th>Actions as ADMIN</th>
      </tr>
      </thead>
      <tbody>
      <tr *ngFor="let user of users">
        <td>{{ user.name + " " + user.lastName }}</td>
        <td *ngIf="isDetailsVisible">{{ user.email }}</td>
        <td *ngIf="isDetailsVisible">{{ user.password }}</td>
        <td>
          <button (click)="getUser(user.id)">Get</button>
          <button (click)="navigateToUpdateForm(user.id)">Update</button>
          <button (click)="deleteUser(user.id)">Delete</button>
        </td>
      </tr>
      </tbody>
    </table>
    <div *ngIf="selectedUser">
      <h2>Selected User Details</h2>
      <p>Name: {{ selectedUser.name + " " + selectedUser.lastName }}</p>
      <p>Email: {{ selectedUser.email }}</p>
      <p>Password: {{ selectedUser.password }}</p>
    </div>
    <button (click)="getAllUserDetails()">Get All Users</button>
    <button (click)="getAllUsers(this.currentPage, this.pageSize)">Refresh Users</button>
    <button (click)="closeDetails()">Close Details</button>
    <div class="slc">
      <label for="pageSize">Users per page:</label>
      <select id="pageSize" (change)="onPageSizeChange($event)">
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="20">20</option>
      </select>
    </div>
    <div>
      <button (click)="previousPage()" [disabled]="currentPage === 1">Previous</button>
      <button (click)="nextPage()" [disabled]="!hasNextPage">Next</button>
    </div>

  `,
  styles: [`
    table {
      width: 100%;
      border-collapse: collapse;
    }

    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: left;
    }

    th {
      background-color: #f2f2f2;
    }

    button {
      margin-right: 5px;
    }

    button:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .slc {
      margin-top: 2rem;
    }

    .searching {
      padding: 0.5rem 5rem;
      margin-bottom: 10px;
    }

    .hidden {
      display: none;
    }
  `]
})

export class DashboardComponent implements OnInit {
  users: UserDTO[] = [];
  isAdmin: boolean = false;
  selectedUser: UserDTO | null = null;
  isDetailsVisible: boolean = false;
  pageSize: number = 5 // default page size
  currentPage: number = 1;
  totalElements: number = 0;
  totalPages: number = 0;
  hasNextPage: boolean = false;
  searchText: string = '';
  isSearching: boolean = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router) {
  }

  ngOnInit() {
    this.getAllUsers(1, this.pageSize);
    this.checkUserRole();
  }


  /*
  In default, we set currentPage to set 1, and pageSize set to 5.
  if the number of users length equal to page size, we estimate there are more users. in e.g
  if current page is equal to 2 and if we take 10 users, this would estimate there are more users up to 30.
  otherwise we set to 20
   */
  getAllUsers(currentPage: number, pageSize: number) {
    if (this.isSearching && this.searchText) {
      this.searchUsers(this.searchText, currentPage, pageSize);
    } else {
      this.userService.getAllUsers(currentPage, pageSize).subscribe({
        next: (users: UserDTO[]) => {
          this.users = users;
          this.updatePaginationState(users.length);
          console.log("Users for page", currentPage, ":", this.users);
        },
        error: (err: HttpErrorResponse) => err.error
      });
    }
  }


  /*
  get user
   */
  getUser(id: string) {
    if (!id) {
      console.error("Cannot fetch user. Invalid ID provided:", id);
      return;
    }
    this.userService.getUser(id).subscribe({
      next: (user: UserDTO) => {
        this.selectedUser = user;
      },
      error: (e) => console.error("Error fetching user:", e),
    });
  }

  /*
  delete user
   */
  deleteUser(id: string) {
    if (!id) {
      console.error("Cannot delete user. Invalid ID provided:", id);
      return;
    }
    this.userService.deleteUser(id).subscribe({
      next: () => {
        console.log('User deleted successfully');
        this.getAllUsers(this.currentPage, this.pageSize);
        if (this.selectedUser && this.selectedUser.id === id) {
          this.selectedUser = null;
        }
      },
      error: (error) => console.error('Error deleting user:', error)
    });
  }

  /*
  make text as search text, make current page 1 by default
  check if searching is active
   */
  onSearch(text: string) {
    this.searchText = text;
    this.currentPage = 1;
    this.isSearching = text.trim().length > 0;
    this.searchUsers(this.searchText, this.currentPage, this.pageSize);
//    this.getAllUsers(this.currentPage, this.pageSize);
  }


  searchUsers(query: string, page: number, pageSize: number) {
    this.userService.searchUsers(query, page, pageSize).subscribe({
      next: (response: PagedResponse) => {
        console.log(this.hasNextPage);
        this.users = response.users;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.currentPage;
      },
      error: (err: HttpErrorResponse) => console.error("Error with searching", err),
    });
  }

  // If admin select 5, 10, 20 it will automatically take the value
  // and change pageSize to the current value.
  onPageSizeChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const newSize = parseInt(selectElement.value, 10);
    this.changePageSize(newSize);
  }

  // update pageSize from the selector and getAllUsers
  changePageSize(newSize: number) {
    this.pageSize = newSize;
    this.getAllUsers(this.currentPage, this.pageSize);
  }


  updatePaginationState(receivedUsersCount: number) {
    console.log("rcv-pagesize: ", receivedUsersCount, this.pageSize);
    this.hasNextPage = receivedUsersCount >= this.pageSize;
    console.log("nxt:", this.hasNextPage);
  }


  // check there is a next page or not
  // increase  page size 1 and show users
  nextPage() {
    if (this.hasNextPage) {
      this.currentPage++;
      this.getAllUsers(this.currentPage, this.pageSize);
    } else if(this.currentPage < this.totalPages){
        this.currentPage++;
        this.searchUsers(this.searchText, this.currentPage, this.pageSize);
    }
  }

  // check if we're in page greater than one
  // decrease page size 1 and show users
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.getAllUsers(this.currentPage, this.pageSize);
    }
  }

  /*
  GETTING EACH DETAILS OF EACH USER CLOSING DETAILS
  NAVIGATE TO UPDATE FORM
   */

  navigateToUpdateForm(id: string) {
    this.router.navigate(['/update-user', id]);
  }

  getAllUserDetails() {
    this.isDetailsVisible = true;
    this.selectedUser = null;
  }

  closeDetails() {
    this.isDetailsVisible = false;
    this.selectedUser = null;
  }

  /* CHECK USERS */
  private checkUserRole() {
    const role = this.authService.getUserAuthorities();
    this.isAdmin = role?.includes("DELETE_USER,GET_ALL_USERS,GET_SPECIFIC_USER") ?? false;
  }

}


