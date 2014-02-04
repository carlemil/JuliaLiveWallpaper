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

float cx;
float cy;
float width;
float height;
int precision;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float3 pixel = convert_float4(in[0]).rgb;

    float fx=(float)((x/width)*2.f-1.f);
    float fy=(float)((y/height)*2.f-1.f);
    
    float t=0;
    
    int k=0;
    int COLOR_MULT = (255 / precision);
    
     while(k<precision)
	 {
	    t = fx*fx-fy*fy+cx;
	    fy = 2*fx*fy+cy;
	    fx = t;
	    if (fx*fx+fy*fy >= 4) break;
	    k++;
	 }
    pixel.z = k*COLOR_MULT;
    pixel.y = 255-k*COLOR_MULT;
    pixel.x = k*COLOR_MULT/3;
    
    out->xyz = convert_uchar3(pixel);
}







