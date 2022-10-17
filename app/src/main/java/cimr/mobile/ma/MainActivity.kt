package cimr.mobile.ma

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.gemadec.cimr.activities.PuceActivity
import com.gemadec.cimr.activities.ScanDocumentActivity
import com.gemadec.cimr.classes.CinDocInfo
import com.sega.cimrprojectmigrationtest.R


class MainActivity : AppCompatActivity() {
    private val launcher: ActivityResultLauncher<Intent>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, ScanDocumentActivity::class.java)
        startActivityForResult(intent, 200)
       /* val someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK) {

                }
            })
        someActivityResultLauncher.launch(intent)*/
        //launcher=registerForActivityResult(ActivityResultContracts)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            val photoPath = data!!.extras!!.getString("photoPath")
            val infos: CinDocInfo? = data.extras!!.getParcelable("infos")
            Log.i("Mainactivity", " " + infos)
             val intent = Intent(this, PuceActivity::class.java)
             intent.putExtra("pass_num", infos!!.getPassnumber())
             intent.putExtra("birthdate", infos!!.getBirthdate())
             intent.putExtra("expirydate", infos!!.getExpirydate())
            startActivityForResult(intent, 2)
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            val imgpassport = data!!.getParcelableExtra<Parcelable>("imgpassport") as Bitmap?
            val code = data.getStringExtra("code")
            Log.i("Mainactivity", "imgpassport " + (imgpassport != null))
        } else if (resultCode == RESULT_CANCELED) {
            val code = data!!.getStringExtra("code")
            Log.i("Mainactivity", "code $code")
        }

    }
}