package com.zhanghongshen.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhanghongshen.model.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApUserMapper extends BaseMapper<User> {
}
