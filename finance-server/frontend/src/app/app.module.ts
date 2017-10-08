import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';

import {FileUploadComponent} from "./components/file-upload/file-upload.component";
import {FileService} from "./services/file.service";
import {HttpModule} from "@angular/http";

@NgModule({
  declarations: [
    AppComponent,
    FileUploadComponent
  ],
  imports: [
    BrowserModule, HttpModule
  ],
  providers: [FileService],
  bootstrap: [AppComponent, FileUploadComponent]
})
export class AppModule { }
