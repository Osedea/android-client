/*
 *    Copyright 2016 Barracks Solutions Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.barracks.ota.client.api;

import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.barracks.client.ota.BuildConfig;

/**
 * Created by saiimons on 16-04-06.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class UpdateCheckResponseTest {

    public static void assertValues(UpdateCheckResponse response) {
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());
        Assert.assertTrue(TextUtils.isEmpty(response.getReason()));
        Assert.assertTrue("42".equals(response.getVersionId()));
        Assert.assertTrue("http://barracks.io/".equals(response.getUrl()));
        Assert.assertTrue("deadbeef".equals(response.getHash()));
        Assert.assertEquals(21432144324324322l, response.getSize().longValue());
    }

    private UpdateCheckResponse parseFromResources() throws IOException {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(
                        new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes f) {
                                return "__robo_data__".equals(f.getName());
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> clazz) {
                                return false;
                            }
                        }
                )
                .create();
        ClassLoader.getSystemResource("update_check_response_success.json");
        File f = new File(ClassLoader.getSystemResource("update_check_response_success.json").getPath());
        return gson.fromJson(new FileReader(f), UpdateCheckResponse.class);
    }

    @Test
    public void parseSuccess() throws IOException {
        UpdateCheckResponse response = parseFromResources();
        assertValues(response);
    }

    @Test
    public void parcel() throws IOException {
        UpdateCheckResponse response = parseFromResources();

        Parcel parcel = Parcel.obtain();
        response.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        response = UpdateCheckResponse.CREATOR.createFromParcel(parcel);
        assertValues(response);
    }

    @Test
    public void errorResponse() {
        String error = "Test failure.";
        UpdateCheckResponse response = UpdateCheckResponse.fromError(error);
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isSuccess());
        Assert.assertTrue(error.equals(response.getReason()));
        Assert.assertTrue(TextUtils.isEmpty(response.getHash()));
        Assert.assertTrue(TextUtils.isEmpty(response.getUrl()));
        Assert.assertTrue(TextUtils.isEmpty(response.getVersionId()));
        Assert.assertNull(response.getSize());
    }

}
