/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.clevermore.monitor.shared.utils;


public class StringFormatter {

    public static String formatBytes(long bytes) {
        long kb = bytes;
        if (bytes > 0) {
            kb = bytes / 1024;
        }
        return kb + "K";
    }

    public static String formatMBytes(long bytes) {
        long kb = bytes;
        if (bytes > 0) {
            kb = bytes / 1024 / 1024;
        }
        return String.valueOf(kb);
    }

    public static native String Base64Encode(String data) /*-{
    function encode_base64(data) {
    var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var out = "", c1, c2, c3, e1, e2, e3, e4;
    for (var i = 0; i < data.length; ) {
    c1 = data.charCodeAt(i++);
    c2 = data.charCodeAt(i++);
    c3 = data.charCodeAt(i++);
    e1 = c1 >> 2;
    e2 = ((c1 & 3) << 4) + (c2 >> 4);
    e3 = ((c2 & 15) << 2) + (c3 >> 6);
    e4 = c3 & 63;
    if (isNaN(c2))
    e3 = e4 = 64;
    else if (isNaN(c3))
    e4 = 64;
    out += tab.charAt(e1) + tab.charAt(e2) + tab.charAt(e3) + tab.charAt(e4);
    }
    return out;
    }
    return encode_base64(data);
    }-*/;

    public static native String Base64Decode(String data)/*-{
    function decode_base64(data) {
    var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var out = "", c1, c2, c3, e1, e2, e3, e4;
    for (var i = 0; i < data.length; ) {
    e1 = tab.indexOf(data.charAt(i++));
    e2 = tab.indexOf(data.charAt(i++));
    e3 = tab.indexOf(data.charAt(i++));
    e4 = tab.indexOf(data.charAt(i++));
    c1 = (e1 << 2) + (e2 >> 4);
    c2 = ((e2 & 15) << 4) + (e3 >> 2);
    c3 = ((e3 & 3) << 6) + e4;
    out += String.fromCharCode(c1);
    if (e3 != 64)
    out += String.fromCharCode(c2);
    if (e4 != 64)
    out += String.fromCharCode(c3);
    }
    return out;
    }
    return decode_base64(data);
    }-*/;
}
