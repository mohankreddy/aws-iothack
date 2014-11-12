package com.iothack.reinvent;




import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.google.gson.GsonBuilder;
import com.iothack.reinvent.model.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class Team35 {

    public static void main(String args[]) {

        try {

            Team35 team35 = new Team35();
            team35.sendGet();

        }catch (Exception exception) {

            exception.printStackTrace();
        }
    }

    // HTTP GET request
    private void sendGet() throws Exception {

        String url = "https://api.spark.io/v1/devices/53ff6c066667574851452567/events/team35-activity?access_token=b8008a6c4c2db4033b446525dbe4b6968532ea03";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);


        ResponseHandler responseHandler = new ResponseHandler()
        {
            @Override
            public Object handleResponse(HttpResponse response)throws ClientProtocolException, IOException
            {
                          /**/
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String str = null;
                    while( (str =reader.readLine())!=null)
                    {

                            if(str.startsWith("data:")) {
                                String jsonStr = str.substring(6, str.length());
                                System.out.println(jsonStr);
                                Response data = new Response();
                                try {
                                   data =  new GsonBuilder().serializeNulls().create().fromJson(jsonStr, Response.class);
                                }catch(Exception e) {
                                    e.printStackTrace();
                                }

                                List<String> attribList = Arrays.asList(data.data.split(","));
                                System.out.println(attribList.get(0)+" "+ attribList.get(1));

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                BasicAWSCredentials props = new BasicAWSCredentials(
                                        "AKIAJX23JYRCE6EQW4RA",
                                        "0nZT25xxz5ufMQ1ecQMUXpSBbyXq5gYzC1FSfhoa");
                                try {
                                    AmazonCloudWatchClient cloudWatch = new AmazonCloudWatchClient(props);
                                    cloudWatch.setEndpoint("monitoring.us-east-1.amazonaws.com");
                                    PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest();
                                    putMetricDataRequest.setNamespace("Team35");

                                    Date date = new Date();

                                    Collection<MetricDatum> metricData = new ArrayList<MetricDatum>();

                                    MetricDatum  metricDatum1 = new MetricDatum().withDimensions(new Dimension()
                                            .withName("id").withValue(attribList.get(0))).withMetricName("xval").withValue(Double.parseDouble(attribList.get(1)));

                                    MetricDatum  metricDatum2 = new MetricDatum().withDimensions(new Dimension()
                                            .withName("id").withValue(attribList.get(0))).withMetricName("yval").withValue(Double.parseDouble(attribList.get(2)));


                                    MetricDatum  metricDatum3 = new MetricDatum().withDimensions(new Dimension()
                                            .withName("id").withValue(attribList.get(0))).withMetricName("zval").withValue(Double.parseDouble(attribList.get(3)));


                                    Double compositeMetric = Math.abs(Double.parseDouble(attribList.get(1)))+Math.abs(Double.parseDouble(attribList.get(2)))+Math.abs(Double.parseDouble(attribList.get(2)));
                                    MetricDatum  metricDatum4 = new MetricDatum().withDimensions(new Dimension()
                                            .withName("id").withValue(attribList.get(0))).withMetricName("composite").withValue(compositeMetric);

                                    System.out.println(compositeMetric);
                                    metricData.add(metricDatum1);
                                    metricData.add(metricDatum2);
                                    metricData.add(metricDatum3);
                                    metricData.add(metricDatum4);

                                    putMetricDataRequest.setMetricData(metricData);

                                    cloudWatch.putMetricData(putMetricDataRequest);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else {

                            }



                    }
                }
                return null;
            }
        };
        httpclient.execute(httpget, responseHandler);

    }
}
