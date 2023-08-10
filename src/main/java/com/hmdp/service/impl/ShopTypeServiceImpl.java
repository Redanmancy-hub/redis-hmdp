package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result listShopType(List<ShopType> typeList) {
        //1.从redis中查询商铺类型缓存
        List<String> shopTypeJsons = stringRedisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY, 0, -1);
        //2.判断是否命中缓存
        if (!CollectionUtils.isEmpty(shopTypeJsons)){
            //集合不为空即命中缓存
            //使用stream流将json集合转为bean集合
            List<ShopType> shopTypeList = shopTypeJsons.stream()
                    .map(item -> JSONUtil.toBean(item, ShopType.class))
                    .sorted(Comparator.comparingInt(ShopType::getSort))
                    .collect(Collectors.toList());
            //返回缓存数据
            return Result.ok(shopTypeList);
        }
        //3.未命中缓存则需要查询数据库
        List<ShopType> shopTypes = lambdaQuery().orderByAsc(ShopType::getSort).list();
        //4.判断数据库中是否有数据
        if(CollectionUtils.isEmpty(shopTypes)){
            //不存在则缓存一个空集合，解决缓存穿透
            stringRedisTemplate.opsForValue()
                    .set(CACHE_SHOP_TYPE_KEY, Collections.emptyList().toString(),CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);
            return Result.fail("商铺分类不存在");
        }
        //数据存在的，先写入数据再返回
        //再使用stream流将集合转化为json集合
        List<String> shopTypeCache = shopTypes.stream()
                .sorted(Comparator.comparingInt(ShopType::getSort))
                .map(JSONUtil::toJsonStr)
                .collect(Collectors.toList());
        //只能使用右插入，要保证顺序
        stringRedisTemplate.opsForList().rightPushAll(CACHE_SHOP_TYPE_KEY,shopTypeCache);
        stringRedisTemplate.expire(CACHE_SHOP_TYPE_KEY,CACHE_SHOP_TYPE_TTL,TimeUnit.MINUTES);
        //返回数据库数据
        return Result.ok(shopTypes);
    }
}
