import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { JwtInterceptor } from './jwtinterceptor/jwt.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(
      withInterceptors([JwtInterceptor])
    ),
  ]
}).catch(err => console.error());
