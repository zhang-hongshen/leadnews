package com.zhanghongshen.wemedia.service;

import com.zhanghongshen.model.wemedia.pojo.WmMaterial;
import com.zhanghongshen.wemedia.WemediaApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
class WmMaterialServiceTest {
    @Autowired
    private WmMaterialService service;

    @Test
    void test() {
        service.save(new WmMaterial());
    }
}