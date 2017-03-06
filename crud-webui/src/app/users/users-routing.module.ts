import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { UsersComponent } from './users.component';
import { AuthGuard } from '../common/_guards/auth.guard';

@NgModule({
  imports: [
    RouterModule.forChild([
      { path: 'users', component: UsersComponent, canActivate: [AuthGuard] }
    ])
  ]
})
export class UsersRoutingModule { }
