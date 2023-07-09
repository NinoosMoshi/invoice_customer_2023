import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetpasswordComponent } from './components/resetpassword/resetpassword.component';
import { VerfiyComponent } from './components/verfiy/verfiy.component';

const routes: Routes = [
  {path:"login", component:LoginComponent},
  {path:"register", component:RegisterComponent},
  {path:"resetpassword", component:ResetpasswordComponent},
  {path:"user/verfiy/account/:key", component:VerfiyComponent},
  {path:"user/verfiy/password/:key", component:VerfiyComponent},
  {path:"**", component:LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
