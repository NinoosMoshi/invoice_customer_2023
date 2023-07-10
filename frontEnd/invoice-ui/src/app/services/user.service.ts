import { CustomHttpResponse, Profile } from './../interface/appstates';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly server: string = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // login(email:string, password:string): Observable<CustomeHttpResponse<Profile>>{
  //    return this.http.post<CustomeHttpResponse<Profile>>(`${this.server}/user/login`, {email, password}).pipe(
  //     map(response =>{
  //       return response
  //     })
  //    );
  // }

  login$ = (email: string, password: string) =><Observable<CustomHttpResponse<Profile>>>
      this.http.post<CustomHttpResponse<Profile>>(`${this.server}/user/login`, {email,password,})
      .pipe(tap(console.log), catchError(this.handleError));



    verifyCode$ = (email: string, code: string) => <Observable<CustomHttpResponse<Profile>>>
    this.http.get<CustomHttpResponse<Profile>>(`${this.server}/user/verify/code/${email}/${code}`)
      .pipe(tap(console.log),catchError(this.handleError)
    );




  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      errorMessage = `A client error occurred - ${error.error.message}`;
    } else {
      if (error.error.reason) {
        errorMessage = error.error.reason;
      } else {
        errorMessage = `An error occurred - Error status ${error.status}`;
      }
    }
    return throwError(() => errorMessage);
  }
}
