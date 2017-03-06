import { Injectable } from '@angular/core';
import { User } from '../_models/user';
import { Http, Headers, Response, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

@Injectable()
export class UserService {
  private url = 'http://localhost:8080/users'; 
  private token = 'Bearer ' + JSON.parse(localStorage.getItem('currentUser')).token;
  private headers = new Headers({
      'Accept': ' application/json',
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': this.token
  });
  private options = new RequestOptions({ headers: this.headers });

  constructor(private http: Http) {}

  getAll () {
    return this.http.get(this.url, this.options)
                    .map(this.extractData)
                    .catch(this.handleError);
  }

  add (user: User): Observable<User> {
    let body = JSON.stringify(user);
    return this.http.post(this.url, body, this.options)
                    .map(this.extractData)
                    .catch(this.handleError);              
  }

  update(user: User) {
    let body = JSON.stringify(user);
    return this.http.put(this.url + '/' + user.id, body, this.options)
                    .map((res: Response) => res.json())
                    .catch(this.handleError);
  }

  remove(user: User)  {
    return this.http.delete(this.url + '/' + user.id, this.options)
                    .catch(this.handleError);
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || { };
  }
  
  private handleError (error: any) {
    let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }

}
