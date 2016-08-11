/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.bluewavevision.tracmojo;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

import com.bluewavevision.tracmojo.model.Follower;
import com.bluewavevision.tracmojo.model.RateColor;
import com.bluewavevision.tracmojo.webservice.VolleySetup;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class TracMojoApplication extends Application {
    public ArrayList<RateColor> listRateColor;
    private ArrayList<Follower> listFollowers = new ArrayList<Follower>();

    private ArrayList<Follower> listParticipants = new ArrayList<Follower>();
    public TracMojoApplication(){
        super();
    }

	@Override
	public void onCreate() {
       // Crashlytics.start(this);
        init();
	}

	private void init() {
		initImageLoader(this);
		VolleySetup.init(this);
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.diskCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

    public ArrayList<RateColor> getListRateColor() {
        return listRateColor;
    }

    public void setListRateColor(ArrayList<RateColor> listRateColor) {
        this.listRateColor = listRateColor;
    }

    public String getHash(String rate){
        String hash = "#000000";
        for (int i = 0; i < listRateColor.size(); i++) {
            if(listRateColor.get(i).getRate().equalsIgnoreCase(rate)){
                hash = listRateColor.get(i).getHex();
            }
        }
        return hash;
    }

    public ArrayList<Follower> getListFollowers() {
        return listFollowers;
    }

    public void setListFollowers(ArrayList<Follower> listFollowers) {
        this.listFollowers = listFollowers;
    }

    public ArrayList<Follower> getListParticipants() {
        return listParticipants;
    }

    public void setListParticipants(ArrayList<Follower> listParticipants) {
        this.listParticipants = listParticipants;
    }
}