import { Component, OnInit } from '@angular/core';
import { Http } from '@angular/http';
import { FormGroup, FormControl, Validators, FormBuilder }  from '@angular/forms';

import { UserService } from '../common/_services/user.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.template.html',
  styleUrls: ['./users.style.css']
})
export class UsersComponent implements OnInit {

  private users = [];
  private isLoading = true;

  private user = {};
  private isEditing = false;

  private addUserForm: FormGroup;
  private username = new FormControl("", Validators.required);
  private password = new FormControl("", Validators.required);
  
  private infoMsg = { body: "", type: "info"};

  constructor(private http: Http,
              private _userService: UserService,
              private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.getUsers();
    this.addUserForm = this.formBuilder.group({
      username: this.username,
      password: this.password
    });

  }

  getUsers() {
    this._userService.getAll().subscribe(
      data => this.users = data,
      error => console.log(error),
      () => this.isLoading = false
    );
  }

  addUser() {
    this._userService.add(this.addUserForm.value).subscribe(
      res => {
        var newUser = res;
        this.users.push(newUser);
        this.addUserForm.reset();
        this.sendInfoMsg("User added successfully.", "success");
      },
      error => console.log(error)
    );
  }

  enableEditing(user) {
    this.isEditing = true;
    this.user = user;
  }

  cancelEditing() {
    this.isEditing = false;
    this.user = {};
    this.sendInfoMsg("User editing cancelled.", "warning");
    // reload the users to reset the editing
    this.getUsers();
  }

  editUser(user) {
    this._userService.update(user).subscribe(
      res => {
        this.isEditing = false;
        this.user = user;
        this.sendInfoMsg("User edited successfully.", "success");
      },
      error => console.log(error)
    );
  }

  /*
  deleteUser(user) {
    if(window.confirm("Are you sure you want to permanently delete this User?")) {
      this._userService.remove(user).subscribe(
        res => {
          var pos = this.users.map(user => { return user.id }).indexOf(user.id);
          this.users.splice(pos, 1);
          this.sendInfoMsg("User deleted successfully.", "success");
        },
        error => console.log(error)
      );
    }
  }
  */

  sendInfoMsg(body, type, time = 3000) {
    this.infoMsg.body = body;
    this.infoMsg.type = type;
    window.setTimeout(() => this.infoMsg.body = "", time);
  }

}
