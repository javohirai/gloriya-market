package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqCompleteShippingCancel
{
    public static String[] send(String tpCode, String reason, String orderNumb, String orderDate)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:CompleteShippingCancel";
        final String methodName = "CompleteShippingCancel";

        if (AppCache.USER_INFO == null)
            return new String[]{"-1", "data activity_agent is empty"};

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("ID", 3);
        request.addProperty("CodeClient", tpCode);
        request.addProperty("ReasonOfReturn", reason);
        request.addProperty("DateOfDelivery", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date(System.currentTimeMillis())));
        request.addProperty("CodeShipman", AppCache.USER_INFO.getUserCode());
        request.addProperty("OrderNumber", orderNumb);
        request.addProperty("OrderDate", orderDate);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            String code = response.getPropertyAsString("Code");
            String msg = response.getPropertyAsString("Message");

            L.info("code.: " + code);
            L.info("msg..: " + msg);

            return new String[]{code, (code.equals("0") ? "successfully send" : msg)};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[]{"-1", (ex.getMessage() != null ? ex.getMessage() : "Exception Unknown")};
        }
    }
}