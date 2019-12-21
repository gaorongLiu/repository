package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.goods.GoodsCode;
import com.changgou.common.util.IdWorker;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SkuMapper skum;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }


    /**
     * 修改
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Spu>) spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Spu>) spuMapper.selectByExample(example);
    }

    /**
     * 传回spu和sku参数处理
     *
     * @param goods
     */
    @Override
    public void add(Goods goods) {
        Spu spu = goods.getSpu();
        long l = idWorker.nextId();
        //设置雪花id
        spu.setId(String.valueOf(l));
        //设置销量
        spu.setSaleNum(0);
        spu.setCommentNum(0);
        spu.setIsMarketable("0");
        spu.setIsEnableSpec("0");
        spu.setIsDelete("1");
        spu.setStatus("0");
        spuMapper.insert(spu);
        //设置sku
        saveSku(goods);

    }

    @Override
    public Goods findGoodsById(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        List<Sku> skus = skum.selectByExample(example);
        Goods goods =new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skus);
        return goods;
    }

    /**
     * 修改
     * @param goods
     */
    @Override
    public void update(Goods goods) {
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKey(spu);

        Example example=new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spu.getId());
        skum.deleteByExample(example);
    }

    @Override
    public void audit(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_FIND_ERROR);
        }
        if ("1".equals(spu.getIsDelete())){
            ExceptionCast.cast(GoodsCode.GOODS_HAS_BEEN_BEND);
        }
        spu.setStatus("1");
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 商品下架
     * @param id
     */
    @Override
    public void pull(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            ExceptionCast.cast(GoodsCode.GOODS_HAS_BEEN_BEND);
        }
        if (spu.getIsDelete().equals("1")){
            ExceptionCast.cast(GoodsCode.GOODS_HAS_BEEN_BEND);
        }
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKey(spu);
    }

    @Override
    public void put(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_FIND_ERROR);
        }
        if (spu.getStatus().equals("0")){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_CHECK);
        }
        if (spu.getIsDelete().equals("1")){
            ExceptionCast.cast(GoodsCode.GOODS_HAS_BEEN_BEND);
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKey(spu);
    }

    @Override
    public void dyDelete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_FIND_ERROR);
        }
        if (spu.getIsMarketable().equals("1")){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_SOLDOUT);
        }
        spu.setIsDelete("1");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKey(spu);
    }

    @Override
    public void unDelete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu.getIsDelete().equals("1")){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_DELETE);
        }
        spu.setIsDelete("0");
        spu.setStatus("0");
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKey(spu);
    }

    @Override
    public void reDelete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_FIND_ERROR);
        }
        if (spu.getIsDelete().equals("0")){
            ExceptionCast.cast(GoodsCode.GOODS_NOT_AT_DELETEAREA);
        }
        spuMapper.deleteByPrimaryKey(spu);
    }

    private void saveSku(Goods goods) {
        Spu spu = goods.getSpu();
        List<Sku> skuList = goods.getSkuList();
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //添加分类与品牌之间的关联

        Integer category3id = category.getId();
        Integer brandId = spu.getBrandId();
        CategoryBrand categoryBrand=new CategoryBrand(category3id,brandId);
        int count = categoryBrandMapper.selectCount(categoryBrand);
        //判断是否有这个品牌和分类的关系数据
        if(count == 0) {
            //如果没有关系数据则添加品牌和分类关系数据
            categoryBrandMapper.insert(categoryBrand);
        }


        Date date = new Date();
        if (skuList != null) {
            for (Sku sku : skuList) {
                //设置主键
                sku.setId(String.valueOf(idWorker.nextId()));
//设置sku规格
                if (sku.getSpec() == null || "".equals(sku.getSpec())) {
                    sku.setSpec("{}");
                }
                //设置sku mane
                String name = spu.getName();
//将规格json字符串转换成Map
                Map<String, String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
                if (specMap != null && specMap.size() > 0) {
                    for (String value : specMap.values()) {
                        name += " " + value;
                    }
                }
                sku.setName(name);//名称
                sku.setSpuId(spu.getId());
                sku.setCreateTime(date);
                sku.setUpdateTime(date);
                sku.setCategoryId(category.getId());
                sku.setCategoryName(category.getName());
                sku.setBrandName(brand.getName());
                skum.insert(sku);
            }
        }
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andEqualTo("sn", searchMap.get("sn"));
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andEqualTo("isMarketable", searchMap.get("isMarketable"));
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
