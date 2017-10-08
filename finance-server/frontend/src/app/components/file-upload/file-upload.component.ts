import {Component, OnInit, Input, Output, EventEmitter, HostListener} from '@angular/core';

import {FileService} from '../../services/file.service';
import * as FileSaver from 'file-saver';
import {RequestOptions, ResponseContentType, Headers} from "@angular/http";

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html'
})
export class FileUploadComponent implements OnInit {

  constructor(private fileService: FileService) {
  }

  ngOnInit() {
  }

  uploadDatasource(fileInput: any) {

    let file = fileInput.target.files[0];

    if (file) {
      let reader: FileReader = new FileReader();

      reader.onloadend = (e) => {
        let base64String = reader.result;
        console.log(base64String);

        let headers = new Headers();
        // headers.append('Content-Type', 'application/json');
        // headers.append('responseType', 'arrayBuffer');

        let options = new RequestOptions({headers: headers});
        options.responseType = ResponseContentType.Blob;

        this.fileService.uploadDatasource(base64String, options)
          .subscribe(
            res => {
              FileSaver.saveAs(res, "file.csv");
            },
            error => {
              console.log('error', error)
            });
      };
      reader.readAsDataURL(file);
      // reader.readAsBinaryString(file);
    }
  }

}
