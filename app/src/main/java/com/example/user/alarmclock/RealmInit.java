package com.example.user.alarmclock;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import android.app.Application;

public class RealmInit extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("wakemeup.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}

