package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.bson.types.ObjectId;
import org.hibernate.mapping.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;

//    @Autowired
//    GridFSBucket gridFSBucket;

    @Test
    public void testFindPage() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.print(all);
    }

    @Test
    public void testRestTemplate() {
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a795d82dd573c3574ee3360", Map.class);
        System.out.print(forEntity);
    }

    @Test
    public void testGridFs() throws FileNotFoundException {
        // 要储存的文件
        File file = new File("D:/czz/study/test/index_banner.ftl");
        // 定义输入流
        FileInputStream inputStream = new FileInputStream(file);
        // 想gridFs存储文件
        ObjectId objectId = gridFsTemplate.store(inputStream, "轮播图测试文件01", "");
        // 得到文件id
        String fileId = objectId.toString();
        System.out.print("fileId:" + fileId);
        System.out.print(file);
    }

    @Test
    public void queryFile() {

    }
}
