/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma version(1)
#pragma rs java_package_name(se.kjellstrand.julia)

float cx;
float cy;
float width;
float height;
float scale;
int precision;

uchar *color;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float fx = (float) ((x / width) * 4.f - 2.f) * scale;
    float fy = (float) ((y / height) * 2.f - 2.f) * scale;
    
    float t = 0;
    int k = 0;
    
    while(k < precision) {
	    t = fx * fx - fy * fy + cx;
	    fy = 2 * fx * fy + cy;
	    fx = t;
	    if (fx * fx + fy * fy >= 4) {
	       break;
	    }
	    k++;
	}

	if (k < precision) {
    	out->b = color[k*3+0];
    	out->g = color[k*3+1];
    	out->r = color[k*3+2];
	} else {
		out->b = 1;
    	out->g = 1;
    	out->r = 1;
	}
}
