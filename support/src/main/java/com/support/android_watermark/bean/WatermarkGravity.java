/*
 *    Copyright 2018 Yizheng Huang
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.support.android_watermark.bean;


public class WatermarkGravity {

    public static final int NONE = 1;
    public static final int TOP_RIGHT = 2;
    public static final int TOP_LEFT = 3;
    public static final int BOTTOM_RIGHT = 4;
    public static final int BOTTOM_LEFT = 5;
    public static final int CENTER = 6;

    private float margin;
    private int gravity = NONE;

    /**
     * Constructors for WatermarkImage
     */
    public WatermarkGravity(float margin,
                            int gravity) {
        this.margin = margin;
        this.gravity = gravity;
    }

    public float getMargin() {
        return margin;
    }

    public WatermarkGravity setMargin(float margin) {
        this.margin = margin;
        return this;
    }

    public double getGravity() {
        return gravity;
    }

    public WatermarkGravity setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }
}
