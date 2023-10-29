package com.wsj.apisingle;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import com.wsj.SMMSClient;
import com.wsj.reponse.ImageItem;
import com.wsj.reponse.ResponseUserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SmmsSDKTest {

    public static final String  smmsToken = "";

    // new client
    final SMMSClient smmsClient = new SMMSClient(smmsToken);
    //or
    //final SMMSClient smmsClient = new SMMSClient("USERNAME", "PASSWORD");

    @Test
    void getProfile(){
        // get user profile
        final ResponseUserProfile profile = smmsClient.profile();
        System.out.println(profile);
    }

    @Test
    void upload() {
        // pic upload
        final ImageItem imageItem = smmsClient.upload(FileUtil.file("lina.jpg"));
        //final String url = imageItem.getUrl();
        //final String hash = imageItem.getHash();
        Console.log(imageItem);

        // pic delete
        final Boolean delete = smmsClient.delete(imageItem.getHash());
        Console.log(delete);
    }

    @Test
    void uploadHistory() {
        // list current user upload history
        final List<ImageItem> imageItems = smmsClient.uploadHistory();
        Console.log(imageItems);
    }
}
