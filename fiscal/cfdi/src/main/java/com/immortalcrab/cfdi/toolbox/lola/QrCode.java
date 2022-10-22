package com.immortalcrab.cfdi.toolbox.lola;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

class QrCode {

    public static ByteArrayInputStream render(String text, int width, int height) throws Exception {
 
        QRCodeWriter qcWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qcWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        var out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
