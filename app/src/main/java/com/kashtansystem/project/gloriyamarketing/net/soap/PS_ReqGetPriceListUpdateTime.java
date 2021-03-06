package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class PS_ReqGetPriceListUpdateTime {
    public static void load(Context context)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetRefusalList";
        final String methodName = "PS_GetPriceListUpdateTime";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            AppDB.getInstance(context).savePriceListUpdateTime(response);
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load organization <<<");
            ex.printStackTrace();
        }
        loadLastSellDate(context);

    }

    public static void loadLastSellDate(Context context) {
        // TODO тут же загружу ограничения дату отгрузки
        final String soapAction_sell = "http://www.sample-package.org#MobileAgents:GetRefusalList";
        final String methodName_sell = "PS_GetLastSellDate";

        SoapObject request_sell = new SoapObject(C.SOAP.NAME_SPACE, methodName_sell);

        SoapSerializationEnvelope envelope_sell = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope_sell.dotNet = true;
        envelope_sell.setOutputSoapObject(request_sell);

        HttpTransportSE httpTransportSELL = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSELL.call(soapAction_sell, envelope_sell);
            SoapObject responses = (SoapObject)envelope_sell.getResponse();
            AppDB.getInstance(context).saveLastSellDate(responses);
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load organization <<<");
            ex.printStackTrace();
        }
    }
}
