package com.dpi.map.cabin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dpi.map.cabin.common.CommonResult;
import com.dpi.map.cabin.entity.DpiTile;
import com.dpi.map.cabin.mapper.DpiTileMapper;
import com.dpi.map.cabin.service.DpiHdvtTileService;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chengxirui
 * @Date: 2023-04-28  09:11
 */
@Slf4j
@Service
public class DpiHdvtTileServiceImpl extends ServiceImpl<DpiTileMapper, DpiTile> implements DpiHdvtTileService {

    @Resource
    private DpiTileMapper dpiTileMapper;

    @Override
    public CommonResult queryPlain(BigDecimal tileX, BigDecimal tileY,
                                   BigDecimal tileZ, String serverId,
                                   String taskId, String label, String layerNames) throws InterruptedException {
//        String key = tileX + "_" + tileY + "_" + tileZ;
        // TODO redis方案不好，后续替换
//        List<String> redisValueList = redisServer.getCacheMapValue(serverId, key);
//        if (CollectionUtil.isNotEmpty(redisValueList)) {
//            log.info("queryPlain HDVT从redis取数据成功，serverId：{}, listSize:{}", serverId, redisValueList.size());
//            return CommonResult.ok(redisValueList);
//        }
        LambdaQueryWrapper<DpiTile> queryWrapper = new LambdaQueryWrapper<>();
        if (tileX != null) {
            queryWrapper.eq(DpiTile::getTileX, tileX);
        }
        if (tileY != null) {
            queryWrapper.eq(DpiTile::getTileY, tileY);
        }
        if (tileZ != null) {
            queryWrapper.eq(DpiTile::getTileZ, tileZ);
        }
        queryWrapper.eq(DpiTile::getServerId, serverId);
        if (StringUtils.isNoneBlank(taskId)) {
            queryWrapper.eq(DpiTile::getTaskId, taskId);
        }
        if (StringUtils.isNoneBlank(label)) {
            queryWrapper.eq(DpiTile::getLabel, label);
        }
        if (StringUtils.isNoneBlank(layerNames)) {
            if (!layerNames.contains(",")) {
                queryWrapper.eq(DpiTile::getLayerName, layerNames);
            } else {
                List<String> getLayerNameList = Splitter.on(",").trimResults().splitToList(layerNames);
                queryWrapper.in(DpiTile::getLayerName, getLayerNameList);
            }

        }
        queryWrapper.eq(DpiTile::getStopFlag, 0);
        queryWrapper.select(DpiTile::getTileData);
        List<DpiTile> dpiTileList = dpiTileMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(dpiTileList)) {
            log.info("HDVT数据为空，serverId：{}", serverId);
            return CommonResult.ok(dpiTileList);
        }
        return CommonResult.ok(dpiTileList.stream().map(DpiTile::getTileData).collect(Collectors.toList()));
    }
}
