package com.zhanghongshen.search.service.impl;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.search.dto.HistorySearchDto;
import com.zhanghongshen.search.context.UserContextHolder;
import com.zhanghongshen.model.search.pojo.UserSearchHistory;
import com.zhanghongshen.search.service.UserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSearchHistoryServiceImpl implements UserSearchHistoryService {

    private MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insert(String keyword, Long userId) {
        // step 1: query whether the keyword exist
        UserSearchHistory userSearchHistory = mongoTemplate.findOne(
                Query.query(Criteria.where("userId").is(userId)
                        .and("keyword").is(keyword)), UserSearchHistory.class);

        // step 2: if the keyword existed, then updated the field 'createTime'
        if(userSearchHistory != null){
            userSearchHistory.setCreateTime(new Date());
            mongoTemplate.save(userSearchHistory);
            return;
        }

        userSearchHistory = new UserSearchHistory();
        userSearchHistory.setUserId(userId);
        userSearchHistory.setKeyword(keyword);
        userSearchHistory.setCreateTime(new Date());

        // step 3: if the keyword didn't exist,
        // then check whether the number of search history records exceeds the limitation(current: 10)
        List<UserSearchHistory> userSearchHistoryList = mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId))
                        .with(Sort.by(Sort.Direction.DESC,"createTime")),
                UserSearchHistory.class);

        // step 4: insert a record or replace the oldest record
        if(userSearchHistoryList.size() < 10){
            mongoTemplate.save(userSearchHistory);
        } else {
            UserSearchHistory lastUserSearchHistory = userSearchHistoryList.get(userSearchHistoryList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearchHistory.getId())), userSearchHistory);
        }
    }

    @Override
    public ResponseResult listSearchHistory() {

        //根据用户查询数据，按照时间倒序
        List<UserSearchHistory> userSearchHistoryList = mongoTemplate.find(Query.query(
                Criteria.where("userId").is(UserContextHolder.getUserId()))
                .with(Sort.by(Sort.Direction.DESC, "createTime")), UserSearchHistory.class);
        return ResponseResult.success(userSearchHistoryList);
    }


    @Override
    public ResponseResult deleteUserSearchHistory(HistorySearchDto dto) {

        mongoTemplate.remove(Query.query(
                Criteria.where("userId").is(UserContextHolder.getUserId())
                .and("id").is(dto.getId())), UserSearchHistory.class);
        return ResponseResult.success(AppHttpCodeEnum.SUCCESS);
    }
}
