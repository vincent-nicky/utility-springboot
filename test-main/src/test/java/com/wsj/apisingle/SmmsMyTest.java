package com.wsj.apisingle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

@SpringBootTest
public class SmmsMyTest {

    @Test
    void smmsUpload() {
        // 使用unirest
        // smms api：https://doc.sm.ms/#api-Image-Upload
        String url = "https://smms.app/api/v2";
        File file = new File("C:\\Users\\86178\\Desktop\\file\\win11壁纸\\61 (小).jpg");//或者是前端传来的图片数据
        HttpResponse<String> response = Unirest.post(url + "/upload")
                .header("Authorization", "")
                .field("smfile", file)
                .asString();

        System.out.println("\n=================== JSON =================");
        System.out.println(response.getBody());

        // 使用TypeToken来保留Map<String, Object>的类型信息
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        // 将JSON字符串转换为Map
        Map<String, Object> map = new Gson().fromJson(response.getBody(), type);

        // 打印Map内容
        System.out.println("\n=================== JSON一层 转 map =================");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        if (!(Boolean) map.get("success")) { //失败
            System.out.println("\n=================== JSON 转 map =================");
            System.out.println(map.get("images"));
        } else { // 成功
            System.out.println("\n=================== 获取map的第一层 =================");
            System.out.println(map.get("code"));
            System.out.println(map.get("message"));
            System.out.println(map.get("data"));

            System.out.println("\n=================== JSON二层 转 map =================");
            Map<String, Object> mapData =  (Map<String, Object>) map.get("data");

            // 打印Map内容
            for (Map.Entry<String, Object> entry : mapData.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        }

        /*
        响应的格式（json转对象后）
        success: true
        code: success
        message: Upload success.
        data: {file_id=0.0, width=768.0, height=480.0, filename=61 (小).jpg, storename=3rcBTJWCgmsp7RX.jpg, size=75728.0, path=/2023/10/17/3rcBTJWCgmsp7RX.jpg, hash=TWONx1piBn8J9CFP2YDKtVlek5, url=https://s2.loli.net/2023/10/17/3rcBTJWCgmsp7RX.jpg, delete=https://smms.app/delete/TWONx1piBn8J9CFP2YDKtVlek5, page=https://smms.app/image/3rcBTJWCgmsp7RX}
        RequestId: 23508C30-20F5-4A30-B499-E9326122AF4F
        */

        /*
         * 文件相同，名字不同 --- 被识别为同一个文件
         * 文件不同，名字相同 --- 被识别为不同的文件
         * */
    }

    @Test
    void smmsDelete() {
        // 使用unirest
        String url = "https://smms.app/api/v2";
        String imgHash = "BrpaKgn4iTWxc3DOwIfMSkY8Qs";
        HttpResponse<String> response = Unirest.get(url + "/delete/" + imgHash)
                .header("Authorization", "")
                .asString();

        System.out.println("\n=================== JSON =================");
        System.out.println(response.getBody());

        // 使用TypeToken来保留Map<String, Object>的类型信息
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        // 将JSON字符串转换为Map
        Map<String, Object> map = new Gson().fromJson(response.getBody(), type);

        // 打印Map内容
        System.out.println("\n=================== JSON一层 转 map =================");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\n=================== map中获取单个 =================");
        System.out.println(map.get("success"));
        System.out.println(map.get("message"));
    }
}
