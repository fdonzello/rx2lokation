package io.fraway.android.libs.observables

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
abstract class BaseObservable<T>(private var compatActivity: AppCompatActivity) : ObservableOnSubscribe<T> {

    abstract fun run(e: ObservableEmitter<T>)

    @SuppressLint("MissingPermission")
    override fun subscribe(e: ObservableEmitter<T>) {
        val targetSdkVersion = compatActivity.applicationContext.applicationInfo.targetSdkVersion

        if (targetSdkVersion >= android.os.Build.VERSION_CODES.M) {
            // need permission checks
            RxPermissions(compatActivity)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .filter({ a -> a })
                    .subscribe({ granted ->
                        if (granted) {
                            run(e)
                        } else {
                            e.onError(SecurityException("Permission has not been granted."))
                        }
                    })
            return
        }

        run(e)
    }
}