package com.changgou.goods.dao;

import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {

    @Select("SELECT name,options from tb_spec WHERE template_id = (SELECT id FROM tb_template WHERE name=#{categoryName})")
    List<Map> findListByName(String Name);
}
