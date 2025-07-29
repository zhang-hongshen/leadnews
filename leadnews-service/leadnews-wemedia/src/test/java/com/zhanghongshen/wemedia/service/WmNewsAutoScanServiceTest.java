package com.zhanghongshen.wemedia.service;

import com.zhanghongshen.wemedia.WemediaApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
class WmNewsAutoScanServiceTest {

    @Autowired
    private WmNewsAutoScanService service;

    @Test
    void autoScanWmNews() {

        service.autoScanWmNews(6232L);
    }
}