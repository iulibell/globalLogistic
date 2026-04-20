package com.tms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tms.dao.TmsLineDao;
import com.tms.dto.TmsLineDto;
import com.tms.entity.TmsLine;
import com.tms.service.TmsLineService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TmsLineServiceImpl implements TmsLineService {
    @Resource
    private TmsLineDao tmsLineDao;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public TmsLineDto getLineDetail(String origin, String dest) {
        StpUtil.checkPermissionOr("driver","manager");
        StpUtil.checkLogin();
        TmsLine tmsLine;
        if(tmsLineDao.selectOne(new LambdaQueryWrapper<TmsLine>()
                .eq(TmsLine::getOrigin,origin)
                .eq(TmsLine::getDest,dest)) != null)
            tmsLine = tmsLineDao.selectOne(new LambdaQueryWrapper<TmsLine>()
                    .eq(TmsLine::getOrigin,origin)
                    .eq(TmsLine::getDest,dest));
        else tmsLine = tmsLineDao.selectOne(new LambdaQueryWrapper<TmsLine>()
                .eq(TmsLine::getOrigin,dest)
                .eq(TmsLine::getDest,origin));
        TmsLineDto tmsLineDto = new TmsLineDto();
        BeanUtils.copyProperties(tmsLine,tmsLineDto);
        return tmsLineDto;
    }

    @Override
    public TmsLineDto getLineById(Long id) {
        StpUtil.checkPermissionOr("manager","driver");
        StpUtil.checkLogin();
        TmsLineDto tmsLineDto = new TmsLineDto();
        BeanUtils.copyProperties(tmsLineDao.selectOne(new LambdaQueryWrapper<TmsLine>()
                .eq(TmsLine::getId,id)),tmsLineDto);
        return tmsLineDto;
    }

    @Override
    public List<TmsLineDto> getLine(int pageNum, int pageSize) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        IPage<TmsLine> page = new Page<>(pageNum,pageSize);
        tmsLineDao.selectPage(page,new LambdaQueryWrapper<TmsLine>()
                .eq(TmsLine::getStatus,(short)1));
        return page.convert(tmsLine -> {
            TmsLineDto tmsLineDto = new TmsLineDto();
            BeanUtils.copyProperties(tmsLine,tmsLineDto);
            return tmsLineDto;
        }).getRecords();
    }

    @Override
    public void addLine(TmsLineDto tmsLineDto) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        Long lineId = snowflakeIdGenerator.nextId();
        TmsLine tmsLine = new TmsLine();
        BeanUtils.copyProperties(tmsLineDto,tmsLine);
        tmsLine.setLineId(lineId);
        tmsLineDao.insert(tmsLine);
    }

    @Override
    public void updateLine(TmsLineDto tmsLineDto) {
        StpUtil.checkPermission("manager");
        StpUtil.checkLogin();
        TmsLine tmsLine = new TmsLine();
        BeanUtils.copyProperties(tmsLineDto,tmsLine);
        tmsLineDao.update(new LambdaUpdateWrapper<TmsLine>()
                .eq(TmsLine::getLineId,tmsLineDto.getLineId()));
    }
}
