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

typedef struct Palette {
    uchar4 c;
} Palette_t;

Palette_t *palette;

uint32_t *color;

void root(const uchar4 *in, uchar4 *out, uint32_t x, uint32_t y) {
    float fx = (float) ((x / width) * 2.f - 1.f) * scale;
    float fy = (float) ((y / height) * 2.f - 1.f) * scale;
    
    float t = 0;
    int k = 0;
    int COLOR_MULT = (255 / (precision + 1));
    
     while(k < precision) {
	    t = fx * fx - fy * fy + cx;
	    fy = 2 * fx * fy + cy;
	    fx = t;
	    if (fx * fx + fy * fy >= 4) {
	       break;
	    }
	    k++;
	 }

	//out = color[k];
	uchar4 co;
	 
    out->b = color[k]&255;


//	uchar4 co;
//	co.b= color[k].b; 
//	co.g= color[k].g; 
//	co.r= color[k].r; 
//	co.a= color[k].a; 

    //uchar3 color;
    //color.x = k*COLOR_MULT;
    //color.y = 0;//k*COLOR_MULT;
    //color.z = 0;//k*COLOR_MULT;
    
    //uchar cx = palette[k].c.x;
    //uchar cy = palette[k].c.y;
    //uchar cz = palette[k].c.z;

	//color.x = cx;
	//color.y = cy;
	//color.z = palette[k].c.y;//150;

    
	//out->argb =  co;//convert_uchar3(co);
    //out->xyz = convert_uchar3(color[k]);
    //palette[k].c); 
}
