package com.liu.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.entitis.CommonResult;
import com.liu.entitis.Payment;
import com.liu.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.*;

@Slf4j
@RestController
@RequestMapping("/payment")
/**
 * 支付controller类
 */
@RefreshScope
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private PaymentService paymentService;

    @PostMapping(value = "/create")
	//添加  前
    /**
     * 添加 后
     */
    public CommonResult create(@RequestBody com.liu.entitis.Payment payment){
        boolean  result=paymentService.save(payment);
        log.info("****插入结果："+result);
        if (result){
            return new CommonResult(444,"插入数据库成功",null);
        }else {
            return new CommonResult(200,"插入数据库失败",result);
        }
    }

    //通过id进行查询
    @GetMapping("/get/{id}")
    /**
     * 通过id进行查询
     */
	//通过id查询
    public CommonResult<com.liu.entitis.Payment> getPaymentById(@PathVariable("id") Long id ){
       Payment payment = paymentService.getById(id);
        log.info("****查询结果："+payment);
        if (payment != null){
            return new CommonResult(200,"查询成功"+serverPort,payment);
        }else {
            return new CommonResult(444,"没有对应记录，查询ID："+id,null);
        }
    }


    @GetMapping(value = "/hello")
    @SentinelResource("hello")
    public String hello() {

        log.info("****hello-hello-hello-hello-hello");
        return "Hello Sentinel";
    }

    @GetMapping(value = "/hello1")
    @SentinelResource("hello1")
    public static  String hello1() throws IOException{
        List<String> indexCodes = new ArrayList<>();
        indexCodes.add("ID00205156");
//        indexCodes.add("ID00042714");
//        indexCodes.add("ID00042716");
//        indexCodes.add("ID00061673");
//        indexCodes.add("ID00260180");
//        indexCodes.add("ID00260187");
        HashMap<String, Object> map = new HashMap<>();
        map.put("indexCodes", indexCodes);
        map.put("order", "desc");
        map.put("startTime", "2021-03-01");
        map.put("endTime", "2021-06-26");
//        map.put("startTime", "2007-05-21");
//        map.put("endTime", "2021-08-11");
        // 将map转为json对象
        JSONObject json = new JSONObject(map);
//        String url = "https://mds.mysteel.com/dynamic/order/api/OUHjnK";
        String url = "https://mds.mysteel.com/dynamic/order/api/mIkTWDnc";
//        String url = "https://mds.mysteel.com/dynamic/order/api/mIkTWDnc";
//        System.out.println(JsonPost.send(url, json, "UTF-8"));
        String send = send(url, json, "UTF-8");
        log.info("****hello-hello-hello-hello-hello");
        return "Hello Sentinel";
    }


    @GetMapping(value = "/hello2")
    @SentinelResource("hello2")
    public static  String hello2() throws IOException {
//        doGet("https://api.mokahr.com/api-platform/v1/archiveReasons");
//        httpClientWithBasicAuth("https://api.mokahr.com/api-platform/testApi");
        doGet("https://api.mokahr.com/api-platform/v1/headcount?currentHireMode=1");
        log.info("****hello-hello-hello-hello-hello");
        return "Hello Sentinel";
    }

    /**
     * 发送post请求
     *
     * @param url        路径
     * @param jsonObject 参数(json类型)
     * @param encoding   编码格式
     * @return
     * @throws
     * @throws IOException
     */
    public static String send(String url, JSONObject jsonObject, String encoding) throws ParseException, IOException {
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
        // s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
        //         "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        httpPost.setHeader("accessTokenSign", "Mg6kv3mwrns18aoxfLjRK08sdFja2z7+fkGyzO9FHF5rEG60Ohr651bUN0IGbtdGbKBLSa8354BH8s8MWbTFhfAMI620oqvcdI6ZuV3UYXnQ/lwNRzSCTDSBSMaGcl+cXdkWa0UWeAJS81GRyJg7wQq/vrrlC220fI7PvdpL5/I=");
        httpPost.setHeader("accessTokenSign", "CrO/Cckz8T9B/tN9Bq5lkqilNnsdwqXYP77Lf9E9fmfn91L4kCSuTvCWAkFDgolu9Pxk31ffxL1UoZQmx3QJMdIM3lGc3pdjFdWSyhaxL82HKt/O6c1rUy5tmhiteXJ136Y/k3oOq1j+6YsGmdOtaga8YDRSuESpQyHFP1TNysU=");
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    /**
     * 发送post请求
     *
     * @param url        路径
     * @param jsonObject 参数(json类型)
     * @param encoding   编码格式
     * @return
     * @throws
     * @throws IOException
     */
    public static String sendOkHttp(String url, JSONObject jsonObject, String encoding) throws ParseException, IOException {
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
        // s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
        //         "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        httpPost.setHeader("accessTokenSign", "Mg6kv3mwrns18aoxfLjRK08sdFja2z7+fkGyzO9FHF5rEG60Ohr651bUN0IGbtdGbKBLSa8354BH8s8MWbTFhfAMI620oqvcdI6ZuV3UYXnQ/lwNRzSCTDSBSMaGcl+cXdkWa0UWeAJS81GRyJg7wQq/vrrlC220fI7PvdpL5/I=");
        httpPost.setHeader("accessTokenSign", "CrO/Cckz8T9B/tN9Bq5lkqilNnsdwqXYP77Lf9E9fmfn91L4kCSuTvCWAkFDgolu9Pxk31ffxL1UoZQmx3QJMdIM3lGc3pdjFdWSyhaxL82HKt/O6c1rUy5tmhiteXJ136Y/k3oOq1j+6YsGmdOtaga8YDRSuESpQyHFP1TNysU=");
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    //ganma1
    public static String doGet(String urlStr) throws IOException {

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);

//        String authString = "sEJy7Pw4AdFCx0MmMXuum2x5NxPrNfd7:";
////            Base64
//        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
//        String authStringEnc = new String(authEncBytes);
//        conn.setRequestProperty("Authorization", "Basic " + authStringEnc);//设置Authoriization字段
        conn.setRequestProperty("Authorization", "Basic " + "c0VKeTdQdzRBZEZDeDBNbU1YdXVtMng1TnhQck5mZDc6");//设置Authoriization字段
        conn.setRequestProperty("Content-Type", "application/json");

        //设置body内的参数，put到JSONObject中
        String paramJson = "{\"number\":\"3\",\"managerEmails\":[\"samengling@efort.com.cn\"],\"jobName\":\"设计师\",\"type\":\"planned\",\"needNumber\":10,\"commitment\":\"fulltime\",\"description\":\"立马招人\",\"status\":\"unstart\",\"creatorEmail\":null,\"startDate\":\"2021-07-14T00:00:00.000Z\",\"completeDate\":\"2021-07-18T00:00:00.000Z\",\"education\":\"博士\"}";
        Object o = JSON.toJSON(paramJson);


        conn.connect();

        // 得到请求的输出流对象
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
        writer.write(o.toString());
        writer.flush();

        // 获取服务端响应，通过输入流来读取URL的响应
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        while ((strRead = reader.readLine()) != null) {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();

        // 关闭连接
        conn.disconnect();

        // 打印读到的响应结果
        System.out.println("运行结束：" + sbf.toString());
        return "error";
    }



}