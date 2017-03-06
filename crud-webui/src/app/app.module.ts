import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { CommonModule } from './common/common.module';
import { AppRoutingModule } from './app-routing.module';
import { LoginModule } from './login/login.module';
import { UsersModule } from './users/users.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  bootstrap: [AppComponent],
  imports: [
    CommonModule,
    AppRoutingModule,
    UsersModule,
    LoginModule
  ],
  providers: [
  ]
})
export class AppModule { }
