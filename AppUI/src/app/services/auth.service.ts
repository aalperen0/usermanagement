import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, pipe, tap} from 'rxjs';
import {jwtDecode} from 'jwt-decode';
import {UserDTO} from "./user.service";


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {
  }

  login(email: string, password: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/login`, {email, password}, {responseType: 'text'})
      .pipe(
        tap((token: string) => {
          this.setToken(token);
        })
      );
  }

  register(firstName: string, lastName: string, email: string, password: string): Observable<string> {
    console.log(firstName, lastName, email, password);
    return this.http.post(`${this.baseUrl}/register`, {firstName, lastName, email, password}, {responseType: 'text'}
    )
      .pipe(
        tap((token: string) => {
          this.setToken(token);
        }));
  }

  getUserEmailFromJWT(): string | null{
    const token = this.getToken();
    if(token){
      try{
        const decodeToken: any = jwtDecode(token);
        return decodeToken.sub|| null;
      }catch (error){
        console.error('Error decoding token:', error);
        return null;
      }
    }
    return null;
  }


  getCurrentUserEmail(): string | null{
    return this.getUserEmailFromJWT();
  }

  getUserAuthorities(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        return decodedToken.authorities || null; // Adjust this based on how roles are stored in your JWT
      } catch (error) {
        console.error('Error decoding token:', error);
        return null;
      }
    }
    return null;
  }

  checkUserRole(): boolean {
    const roleAuthorities = this.getUserAuthorities();
    return roleAuthorities?.includes("DELETE_USER,GET_ALL_USERS,GET_SPECIFIC_USER") ?? false;
  }

  /*

  Getting, setting and clearing token from local storage
   */
  setToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }


  clearToken(): void {
    localStorage.removeItem('jwtToken');
  }

}
