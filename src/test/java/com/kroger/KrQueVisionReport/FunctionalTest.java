package com.kroger.KrQueVisionReport;

import com.ibm.mq.*;
import com.ibm.mq.headers.MQRFH2;
import com.org.yaapita.libparsefiles.ParseFiles;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utility.BasicUtil;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static com.ibm.mq.constants.CMQC.*;
import static com.org.yaapita.libloadinputdata.LoadExcelData.getEnabledExcelTests;
import static com.org.yaapita.libmq.MQClient.*;

public class FunctionalTest {

     @DataProvider
       public Object[] getDataFromExcel() {
           return getEnabledExcelTests("\\src\\test\\resources\\TestData_Sheet\\KrQueVisionReport_Functional_Test.xlsx", "Functional_TestData");
       }

      @Test(dataProvider = "getDataFromExcel")
      public void putDataOnMQ(Map<String, String> inputDataMap) throws IOException, InterruptedException, MQException {

       BasicUtil utility = new BasicUtil();
       String requestFilePath = "C:\\Users\\kon2208\\developer\\git\\KrQueVisionReport\\src\\test\\resources\\Input_XML\\";
       String requestFileName = inputDataMap.get("Request_Xml");
       String responseFilePath = "C:\\Users\\kon2208\\developer\\git\\KrQueVisionReport\\src\\test\\resources\\ExpectedResponse\\";
       String responseFileName = inputDataMap.get("Response_Xml");
       BufferedReader bufReader_txt = new BufferedReader(new FileReader(requestFilePath+requestFileName));
      // BufferedReader bufReader_txt = new BufferedReader(new FileReader("C:\\Users\\kon6794\\developer\\git\\KrStoreReportProcessor\\src\\test\\resources\\Input_XML\\KrStoreReportProcessor_SampleInput.xml"));
        StringBuilder encoded_file_txt_Sb = new StringBuilder();
        String line_txt = bufReader_txt.readLine();
        while (line_txt != null) {
            encoded_file_txt_Sb.append(line_txt).append("\n");
            line_txt = bufReader_txt.readLine();
        }

//        String eMessage = encoded_file_txt_Sb.toString();
//        System.out.println("Test ran succesfully"+eMessage);
          String requestFileContent = ParseFiles.readAndParseFile(requestFilePath + requestFileName);
          System.out.println("Request File Content is " + requestFileContent + "\n");

        //PUT data from MQ
          System.out.println( inputDataMap.get("correlationId"));
          System.out.println( inputDataMap.get("transactionId"));
          System.out.println( inputDataMap.get("eventType"));
        MQQueueManager mq = mqConnect("QM.B2C.TEST403", "u060brkt403", "CLNT.TEST403.ADMIN", 1414, "", "");

//        messagePut(mq, "QL.STORE.EVENT.QUEVISIONREPORT", requestFileContent);
//        System.out.println("Message is published on queue");
////        Thread.sleep(10000);
        //GET data from MQ
          MQMessage msg = new MQMessage();
          MQRFH2 rfh2 = new MQRFH2();
          rfh2.setEncoding(MQENC_NATIVE);
          rfh2.setCodedCharSetId(MQCCSI_INHERIT);
          rfh2.setFormat(MQFMT_STRING);
          rfh2.setNameValueCCSID(1208);
          rfh2.setFieldValue("usr", "correlationId", inputDataMap.get("correlationId"));
          rfh2.setFieldValue("usr", "transactionId", inputDataMap.get("transactionId"));
          rfh2.setFieldValue("usr", "eventType", inputDataMap.get("eventType"));

          try {
              rfh2.write(msg);
          } catch (IOException e) {
              System.err.println(e.getLocalizedMessage());
          }
          msg.writeString(requestFileContent);
          //  msg.writeString(eMessage);

          msg.persistence = MQPER_PERSISTENT;
          msg.format = MQFMT_RF_HEADER_2;

          String queueName = "QL.STORE.EVENT.QUEVISIONREPORT";
          MQPutMessageOptions pmo = new MQPutMessageOptions();
          MQQueue queue = mq.accessQueue(queueName, MQOO_FAIL_IF_QUIESCING + MQOO_OUTPUT);
          queue.put(msg, pmo);
          Thread.sleep(3000);

        mq = mqConnect("QM.B2C.TEST403", "u060brkt403", "CLNT.TEST403.ADMIN", 1414, "", "");

        String responseMsg = messageGet(mq, "QL.STORE.EVENT.TOPIC");

        System.out.println("Response :-" + responseMsg);
//
//         if (responseFileName != null) {
//              BufferedReader bufReader_expected = new BufferedReader(new FileReader(responseFilePath + responseFileName));
//              // BufferedReader bufReader_txt = new BufferedReader(new FileReader("C:\\Users\\kon6794\\developer\\git\\KrStoreReportProcessor\\src\\test\\resources\\Input_XML\\KrStoreReportProcessor_SampleInput.xml"));
//              StringBuilder encoded_file_expected_response_Sb = new StringBuilder();
//              String line_txt_expected = bufReader_expected.readLine();
//              while (line_txt_expected != null) {
//                  encoded_file_expected_response_Sb.append(line_txt_expected).append("\n");
//                  line_txt_expected = bufReader_expected.readLine();
//              }
//              String expected_response = encoded_file_expected_response_Sb.toString();
              String result = XMLComparison.XMLValidation(expected_response, responseMsg);
//              System.out.println("Comparison result is: "+ result);
//          }
//        String expectedResponse = inputDataMap.get("Expected_Status_code");
//        Assert.assertEquals(utility.getlog(),expectedResponse,"Expected status code and actual status code do not match");

    }
}
