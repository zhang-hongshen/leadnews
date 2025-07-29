package com.zhanghongshen.mongo;


import com.zhanghongshen.mongo.pojo.AssociateWord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = MongoDemoApplication.class)
@RunWith(SpringRunner.class)
public class MongoTest {


    @Autowired
    private MongoTemplate mongoTemplate;

    //保存
    @Test
    public void saveTest(){
        AssociateWord associateWords = new AssociateWord();
        associateWords.setContent("seosoojin");
        associateWords.setCreateTime(new Date());
        mongoTemplate.save(associateWords);
    }


    @Test
    public void findOneTest(){
        AssociateWord associateWord = mongoTemplate.findById("675e4f6233e14d556ad7c4f8", AssociateWord.class);
        System.out.println(associateWord);
    }


    @Test
    public void conditionalQueryTest(){
        Query query = Query.query(Criteria.where("content").is("seosoojin"))
                .with(Sort.by(Sort.Direction.DESC,"createTime"));
        List<AssociateWord> associateWordsList = mongoTemplate.find(query, AssociateWord.class);
        System.out.println(associateWordsList);
    }

    @Test
    public void deleteTest(){
        mongoTemplate.remove(Query.query(Criteria.where("content").is("seosoojin")), AssociateWord.class);
    }
}
