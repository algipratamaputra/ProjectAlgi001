package com.project.projectalgi001.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.project.projectalgi001.R
import com.project.projectalgi001.admin.ui.listdo.DetailDoCashAdminActivity
import com.project.projectalgi001.user.model.FetchSpkCreditModel
import java.io.File
import java.io.FileOutputStream

class DataToPDFCredit(val model : FetchSpkCreditModel, val context : Context, val width : Int, val height : Int) {

    @SuppressLint("InflateParams")
    fun generatePDF() {
        val doc = PdfDocument()
        val pageInfo : PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(700,1300,1).create()
        val page : PdfDocument.Page = doc.startPage(pageInfo)

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.item_pdf_layout, null)
        view.findViewById<TextView>(R.id.get_name_pdf).text = model.nameBuyerCredit
        view.findViewById<TextView>(R.id.get_address_customerPdf).text = model.addressBuyerCredit
        view.findViewById<TextView>(R.id.get_mobileNumber_pdf).text = model.mobileNumberBuyerCredit
        view.findViewById<TextView>(R.id.get_email_pdf).text = model.emailBuyerCredit
        view.findViewById<TextView>(R.id.get_name_car).text = model.nameCarSoldCredit
        view.findViewById<TextView>(R.id.get_policeNumber_pdf).text = model.policeNumberCarSoldCredit
        view.findViewById<TextView>(R.id.get_machineNumber_pdf).text = model.machineNumberCarSoldCredit
        view.findViewById<TextView>(R.id.get_chassisNumber_pdf).text = model.chassisNumberCarSoldCredit

        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        view.layout(0,0,2480,3508 )

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        Bitmap.createScaledBitmap(bitmap, 595, 842, true)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        page.canvas.drawBitmap(bitmap,0F,0F,null)
        doc.finishPage(page)
        try {
            val file = File(Environment.getExternalStorageDirectory().toString()+ File.separator + "${model.nameCarSoldCredit} ${model.policeNumberCarSoldCredit}.pdf")
            val stream = FileOutputStream(file)

            file.createNewFile()
            doc.writeTo(stream)
            doc.close()

            val intent = Intent(context, DetailDoCashAdminActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("PDF", Environment.getExternalStorageDirectory().toString()+ File.separator + "${model.nameCarSoldCredit} ${model.policeNumberCarSoldCredit}.pdf")
            context.startActivity(intent)
        }
        catch ( e : Exception)
        {
            System.err.println(e.message)
        }

    }

}