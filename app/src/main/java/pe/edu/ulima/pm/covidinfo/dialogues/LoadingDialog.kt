package pe.edu.ulima.pm.covidinfo.dialogues

import android.app.Activity
import android.app.AlertDialog
import pe.edu.ulima.pm.covidinfo.R

class LoadingDialog(val activity: Activity) {

    private lateinit var isDialog: AlertDialog

    fun startLoading() {

        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_dialog, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss() {
        isDialog.dismiss()
    }
}