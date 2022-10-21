package com.gemadec.cimr.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;


import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderInitCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.enums.Scenario;
import com.regula.documentreader.api.enums.eGraphicFieldType;
import com.regula.documentreader.api.errors.DocumentReaderException;
import com.regula.documentreader.api.params.DocReaderConfig;
import com.regula.documentreader.api.results.DocumentReaderResults;

import java.io.IOException;
import java.io.InputStream;

import com.gemadec.cimr.R;
import com.gemadec.cimr.classes.CinDocInfo;
import com.gemadec.cimr.utils.ImageUtil;

public class ScanDocumentActivity extends AppCompatActivity {
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_document);
        try {
            InputStream licInput = getResources().openRawResource(R.raw.regula);
            int available = licInput.available();
            byte[] license = new byte[available];
            licInput.read(license);

            DocumentReader.Instance().initializeReader(ScanDocumentActivity.this, new DocReaderConfig(license), new IDocumentReaderInitCompletion() {

                @Override
                public void onInitCompleted(boolean success, DocumentReaderException error) {

                    if (success) {

                        DocumentReader.Instance().processParams().scenario = Scenario.SCENARIO_MRZ;
                        DocumentReader.Instance().customization().edit().setShowStatusMessages(true).apply();
                        DocumentReader.Instance().processParams().dateFormat = "dd/mm/yyyy";

                        DocumentReader.Instance().showScanner(ScanDocumentActivity.this, completion);
                        // initialization successful
                    } else {
                        Log.i("ScanDocumentActivity", "" + error.getMessage() + R.string.initializing_failed);
                        // initialization was not successful
                    }
                }
            });
            licInput.close();
        } catch (Exception ex) {
            Log.e("ScanDocumentActivity", "" + ex.getMessage());
            ex.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private IDocumentReaderCompletion completion = new IDocumentReaderCompletion() {
        @Override
        public void onCompleted(int action, DocumentReaderResults results, DocumentReaderException error) {
            if (action == DocReaderAction.COMPLETE) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                CinDocInfo infos = new CinDocInfo();
                infos.setFirstName(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_GIVEN_NAMES));
                infos.setLastName(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_SURNAME));
                infos.setIdentifier(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_PERSONAL_NUMBER));
                infos.setBirthdate(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_DATE_OF_BIRTH));
                infos.setExpirydate(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_DATE_OF_EXPIRY));
                infos.setPassnumber(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_DOCUMENT_NUMBER));
                infos.setNationality(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_NATIONALITY));
                infos.setSexe(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_SEX));
                infos.setAddress(results.getTextFieldValueByType(com.regula.documentreader.api.enums.eVisualFieldType.FT_ADDRESS));
                Bitmap documentImage = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("infos", infos);
                try {
                    String portraitImagePath = ImageUtil.savebitmap(documentImage, ScanDocumentActivity.this, "documentImage");

                    bundle.putString("photoPath", portraitImagePath);
                } catch (IOException e) {
                    Log.e("ScanDocumentActivity", "Error while saving the documentImage: " + e.getMessage());
                    e.printStackTrace();
                }

                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();

            }
            if (action == DocReaderAction.CANCEL) {
                cancelCamera();
            } else if (action == DocReaderAction.ERROR) {
                cancelCamera();

            } else {

            }
        }
    };

    private void cancelCamera() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
