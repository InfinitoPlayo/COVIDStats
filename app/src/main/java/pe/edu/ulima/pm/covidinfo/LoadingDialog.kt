package pe.edu.ulima.pm.covidinfo

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog(val activity: Activity) {

    private lateinit var isDialog: AlertDialog

    fun startLoading() {

        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_dialog, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss() {
        isDialog.dismiss()
    }
}