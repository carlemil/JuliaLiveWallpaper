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

//#include "rs_atomic.rsh"
#include "rs_debug.rsh"

float cx;
float cy;
float width;
float height;
float zoom;
int precision;

float pi = 3.14159265359;
float pi4 = 0.78539816339;

float bigNumber = 12345678;

uchar *color;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float fx = (x + 0.5f) / width * (4.f / zoom) - (2.f / zoom);
    float fy = (y + 0.5f) / height * (4.f / zoom) - (2.f / zoom);

    float ox=0;
    float oy=0;

    float t = 0;
    int k = 0;
    float d = 0;
    
    while(k < 15) {
	    t = fx * fx - fy * fy + cx;
	    fy = 2 * fx * fy + cy;
	    fx = t;
	    ox = fx;
	    oy = fy;
        d += sqrt(fabs(ox * ox) + fabs(ox * ox));
	    k++;
	    if (fabs(fx) > bigNumber || fabs(fy) > bigNumber){
	        break;
	    }
	}
	
    //int c = (int)(((sin(1/(d-5))+1)/pi)*precision/2);
    
    //int c = (int)(((1.0/(d-5))+1.0)*precision/2.0);
    
    float col = (sin(1/(d-9)));
    
    if (x==300 & y%10==0) {
       rsDebug("c ", (sin(1/(d-5))+1), d);
	}
	
    //if(c<0){c=0;}
    //if(c>precision){c=precision;}
    if(col<0){col=0.0;}
    if(col>1){col=1.0;}
        
    out->b = (int) (1.0-col * 255.0);
    out->g = (int) (col * 255.0);
    out->r = (int) (col/2.0 * 255.0);

    //out->b = color[c*3+0];
    //out->g = color[c*3+1];
    //out->r = color[c*3+2];
}
