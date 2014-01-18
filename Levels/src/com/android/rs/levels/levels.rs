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
#pragma rs java_package_name(com.android.rs.levels)

float cx_;
float cy_;
float width;
float height;
rs_matrix3x3 colorMat;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float3 pixel = convert_float4(in[0]).rgb;
   
    
    float cx=(float)(cx_/2);
    float cy=(float)(cy_/2);
    // -1;2
    float fx=(float)(x/width)*3-1.f;
    // 1.1;-1.1
    float fy=(float)(y/height)*3-1.5f;
    
    float t=0;
    
    int k=0;
    int PREC = 15;
    
         //x = i; y=j;
	     while(k<PREC)
		 {
		    t = fx*fx-fy*fy+cx;
		    fy = 2*fx*fy+cy;
		    fx = t;
		    if (fx*fx+fy*fy >= 4) break;
		    k++;
		 }
	       //g.setRGB((int)((i+2)*w/4), (int)((j+2)*h/4),colorscheme[k].getRGB());    
    pixel.x = k*16;
    pixel.y = k*16;
    pixel.z = k*16;
    
    
    
    
    out->xyz = convert_uchar3(pixel);
}







