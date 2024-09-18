import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {UpdateUserComponent} from "./user-update-form/user-update-form.component";
import {RegisterComponent} from "./register/register.component";
import {WelcomeComponent} from "./dashboard/welcome/welcome.component";
import {UserProfileComponent} from "./dashboard/userprofile/userprofile.component";

export const routes: Routes = [
  {path: 'welcome', component: WelcomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'dashboard', component: DashboardComponent},
  {path: 'user-profile/:id', component: UserProfileComponent},
  {path: 'update-user/:id', component: UpdateUserComponent},
  {path: '', redirectTo: '/welcome', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
