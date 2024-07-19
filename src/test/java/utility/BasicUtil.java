package utility;


import com.org.yaapita.libapiresthelper.RestResponse;
import com.org.yaapita.libapiresthelper.builder.RequestBuilder;
import io.restassured.response.ResponseBody;
import org.apache.commons.lang.StringUtils;

import static com.org.yaapita.libapiresthelper.RestResponse.getRestResponse;


public class BasicUtil
{/*
    public String buildSoapRequest(String request,String ENDPOINT)
    {
        Map<String, String> mheader = new HashMap<>();
        mheader.put("Authorization","Basic a29uNTEyNDpCaXR3aXNlOA==");
        mheader.put("Content-Type","text/xml;charset=UTF-8");
        mheader.put("soapAction","http://ws.kroger-fmj.com/CustomerTransactionSearch");
        RequestSpecification requestAPI= RestAssured.given();
        Response responseAPI=requestAPI.relaxedHTTPSValidation().headers(mheader).body(request).post(ENDPOINT);
        System.out.println("Response body:"+responseAPI.getBody().asString());
        return responseAPI.getBody().asString();

    }*/
    //Get Actual status code from Timing log
    public static String getlog()
    {

        //requestBuilder requestBuilder_logs = new requestBuilder();
        RequestBuilder requestBuilder_logs = new RequestBuilder();
        requestBuilder_logs.setMethod("GET");
        requestBuilder_logs.setUrl("http://u060brkt403.kroger.com:52000/iiblogs/KrStoreReportProcessor/logs/srp.timing.log");

        String auth = "Basic a29uNjc5NDpCaXR3aXNlMw==";
        requestBuilder_logs.setBasicAuth(auth);
        RestResponse restReponse_log = getRestResponse(requestBuilder_logs);
        ResponseBody responseBody_logs = restReponse_log.getResponse();
        String logs = responseBody_logs.asString();
        //System.out.println("logs  \n"+logs);
        //split each timing log
        String[] Separted_TimmingLog = logs.split("\\n");
        Integer arySizeTimingLog = Separted_TimmingLog.length;
        String Latest_TimingLog = Separted_TimmingLog[arySizeTimingLog - 1].toString();

        System.out.println("latest_logs\n" + Latest_TimingLog);
        //split a single timing log by ‘,’
        String[] TimingLogArry = Latest_TimingLog.split(",");
        String actualStatsCode = StringUtils.substringAfterLast( Latest_TimingLog,"," );
        // System.out.println("Actual status code is : "+actualStatsCode);
        //System.out.println("Executed GetLog method");
        return actualStatsCode;
    }

}
