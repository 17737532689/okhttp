package com.bj.okhttp.test.http.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Controller
public class test {

    @Autowired
    private OkHttpService okHttpService;
        @RequestMapping("/url")
        public String url(){
            String url = "http://127.0.0.1:8080/url2";
            String serviceName = "zhangsan";
            String platformNo = "1234";

            JSONObject jsonObject = new JSONObject();


            jsonObject.put("admin","zhangan");
            jsonObject.put("passwoed","123");
            jsonObject.put("client","123456");

            String reqData =jsonObject.toJSONString();
            String responseBody = okHttpService
                    .doSyncGet(url + "?serviceName=" + serviceName + "&platformNo=" +
                            platformNo + "&reqData=" + reqData);
            return responseBody;
        }




    @RequestMapping("/url2")
    public String url2(@RequestParam String serviceName,
                       @RequestParam String platformNo, @RequestParam String reqData){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("zhangsan","paobu");
        jsonObject.put("xihao","kandianshi");
        jsonObject.put("gongzuo","renzhen");
        return jsonObject.toJSONString();
    }
}
