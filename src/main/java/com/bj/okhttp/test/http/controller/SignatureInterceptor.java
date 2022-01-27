package com.bj.okhttp.test.http.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;

import static org.apache.catalina.filters.Constants.METHOD_GET;

/**
 * okHttp3拦截器, 用于数据签名及验签
 */
@Slf4j
@Service
public class SignatureInterceptor implements Interceptor {


    /**
     * 签名的编码
     */
    private final String INPUT_CHARSET = "UTF-8";

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 拦截请求，获取到该次请求的request
        Request request = chain.request();
        Response response = null;
        Request requestProcess = null;
        // 请求银行存管系统的请求是GET, 这里只处理GET
        if (METHOD_GET.equals(request.method())) {
            // 获取原请求地址
            String url = request.url().toString();
            String base = url.substring(0, url.indexOf("?") + 1);
            // 获取参数解码
            final String serviceName = getParam(url, "serviceName");
            final String platformNo = getParam(url, "platformNo");
            final String reqData = getParam(url, "reqData");

            // 修改请求地址, 并添加签名参数
            url = base + "serviceName=" + URLEncoder.encode(serviceName, INPUT_CHARSET)
                    + "&platformNo=" + URLEncoder.encode(platformNo, INPUT_CHARSET)
                    + "&reqData=" + URLEncoder.encode(reqData, INPUT_CHARSET);

           // 重新构造请求
            Request.Builder builder = request.newBuilder();
            builder.get().url(url);
            requestProcess = builder.build();
            // 执行请求
            response = chain.proceed(requestProcess);

            // 得到结果集,
            final String result = response.body().string();

            JSONObject parseObject = JSON.parseObject(result);

            // 验签成功
            // 重新构造结果集, 原因是response.body().toString();调用后, body中值会请空
          response = response.newBuilder().body(ResponseBody.create(response.body().contentType(), parseObject.toJSONString())).build();


        } else {
            // 其他请求不处理
            response = chain.proceed(request);
        }
        // 返回结果集
        return response;
    }

    /**
     * 获取url中参数
     *
     * @param url
     * @param name
     * @return
     */
    public static String getParam(String url, String name) {
        String params = url.substring(url.indexOf("?") + 1, url.length());
        final String[] splitter = params.split("&");
        for (String str : splitter) {
            if (str.startsWith(name + "=")) {
                return str.substring(name.length() + 1);
            }
        }
        return "";
    }


}