package io.talkplus.sample;

import androidx.multidex.MultiDexApplication;

import io.talkplus.TalkPlus;

public class SampleApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        TalkPlus.init(getApplicationContext(), "875bd0c3-83eb-4086-b7ba-a1a8b05a26fe");
    }
}
