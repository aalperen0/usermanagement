import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {map, Observable, tap} from 'rxjs';
import {AuthService} from "./auth.service";

export interface UserDTO {
  id: string;
  name: string;
  lastName: string;
  email: string;
  password: string;
}


export interface PagedResponse {
  users: UserDTO[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  /*
    Setting bearer token to header
    from local storage.
   */

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
      throw new Error('Token not available');
    }
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  /*
    User Endpoints for getting, updating and deleting users
   */
  getAllUsers(page: number = 1, size: number = 5): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/users?page=${page}&size=${size}`, {headers: this.getHeaders()})
      .pipe(
        map(users => users.map(user => ({
          ...user,
          id: user.id?.toString()
        })))
      );
  }

  getUser(id: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.apiUrl}/users/${id}`, {headers: this.getHeaders()})
      .pipe(
        tap(user => console.log("Fetched user:", user)) // Debug log
      );
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}`, {headers: this.getHeaders()})
      .pipe(
        tap(() => console.log('Delete request sent')) // Debug log
      );
  }


  updateUser(id: string, user: Partial<UserDTO>): Observable<UserDTO> {
    // Only include changed fields in the request
    const changedFields = Object.entries(user).reduce((acc, [key, value]) => {
      if (value !== undefined && value !== null) {
        acc[key as keyof UserDTO] = value;
      }
      return acc;
    }, {} as Partial<UserDTO>);

    return this.http.patch<UserDTO>(`${this.apiUrl}/users/patch/${id}`, changedFields, {headers: this.getHeaders()})
      .pipe(
        tap(updatedUser => console.log('Updated user field:', updatedUser))
      );
  }

  getUserByEmail(email: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.apiUrl}/user/${email}`);
  }


  searchUsers(query: string, page: number = 1, pageSize: number = 5): Observable<PagedResponse> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', pageSize.toString());

    return this.http.get<PagedResponse>(`${this.apiUrl}/search`, {
      params: params,
      headers: this.getHeaders(),
    });
  }

}
