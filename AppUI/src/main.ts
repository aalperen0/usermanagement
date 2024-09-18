import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {bootstrapApplication} from '@angular/platform-browser';
import {AppComponent} from './app/app.component';
import {importProvidersFrom} from '@angular/core';
import {RouterModule} from '@angular/router';
import {HttpClientModule, provideHttpClient, withInterceptors} from '@angular/common/http';
import {JwtInterceptor} from "./app/jwtinterceptor/jwt.interceptor";

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(
      withInterceptors([JwtInterceptor])
    ),
    importProvidersFrom(RouterModule.forRoot([
      {path: 'login', loadComponent: () => import('./app/login/login.component').then(m => m.LoginComponent)},
      {
        path: 'register',
        loadComponent: () => import('./app/register/register.component').then(m => m.RegisterComponent)
      },
      {
        path: 'welcome',
        loadComponent: () => import('./app/dashboard/welcome/welcome.component').then(m => m.WelcomeComponent)
      },

      {
        path: 'user-profile',
        loadComponent: () => import('./app/dashboard/userprofile/userprofile.component').then(m => m.UserProfileComponent)
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./app/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'update-user/:id',
        loadComponent: () => import('./app/user-update-form/user-update-form.component').then(m => m.UpdateUserComponent)
      },

      {path: '', redirectTo: '/welcome', pathMatch: 'full'},
      // Add other routes as needed
    ])),
    importProvidersFrom(HttpClientModule)
  ]
}).catch(err => console.error(err));
