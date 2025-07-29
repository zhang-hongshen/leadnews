package com.zhanghongshen.mongo.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("associate_word")
public class AssociateWord {

    private String id;

    private String content;

    private Date createTime;

}