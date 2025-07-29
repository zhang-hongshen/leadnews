package com.zhanghongshen.model.search.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document("ap_user_search")
public class UserSearchHistory {

    private String id;

    private Long userId;

    private String keyword;

    private Date createTime;

}
