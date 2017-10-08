import {Injectable} from '@angular/core';
import {Headers, Http, RequestOptions, Response, ResponseContentType, URLSearchParams} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {escape, unescape} from "querystring";
import * as FileSaver from 'file-saver';

@Injectable()
export class FileService {

  _baseURL: string = 'http://localhost:8099/csv-parser/stream';

  constructor(private http: Http) {
  }


  uploadDatasource(base64, options): Observable<any[]> {

    return this.http.post(this._baseURL, base64, options)
      .map(res => {
        return res.blob();
      })
      .catch(error => Observable.throw(error))

  }

  b64DecodeUnicode(str) {
    // Going backwards: from bytestream, to percent-encoding, to original string.
    return decodeURIComponent(atob(str).split('').map(function (c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
  }

  // b64EncodeUnicode(str) {
  //   // first we use encodeURIComponent to get percent-encoded UTF-8,
  //   // then we convert the percent encodings into raw bytes which
  //   // can be fed into btoa.
  //   return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
  //     function toSolidBytes(match, p1) {
  //       return String.fromCharCode('0x' + p1);
  //     }));
  // }

  encode_utf8(s) {
    return unescape(encodeURIComponent(s));
  }

  decode_utf8(s) {
    return decodeURIComponent(escape(s));
  }
}
