package com.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.admin.dao.DictionaryDao;
import com.admin.dto.DictionaryDto;
import com.admin.dto.DictionaryOperationDto;
import com.admin.entity.Dictionary;
import com.admin.service.DictionaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Resource
    private DictionaryDao dictionaryDao;

    @Override
    @Transactional
    public void addDictionary(List<DictionaryDto> dictionaryDtoList) {
        List<Dictionary> dictList = dictionaryDtoList.stream().map(dto -> {
            Dictionary dictionary = new Dictionary();
            BeanUtil.copyProperties(dto,dictionary);
            return dictionary;
        }).toList();
        dictionaryDao.insert(dictList);
    }

    @Override
    public List<DictionaryDto> getDictionaryList(String dictType) {
        List<Dictionary> dictList = dictionaryDao.selectList(
                new LambdaQueryWrapper<Dictionary>()
                        .eq(Dictionary::getDictType,dictType)
                        .eq(Dictionary::getStatus, (short) 1)
                        .orderByAsc(Dictionary::getSort));
        return dictList.stream().map(dict -> {
            DictionaryDto dto = new DictionaryDto();
            BeanUtil.copyProperties(dict,dto);
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void updateDictionary(DictionaryOperationDto dictionaryOperationDto) {
        Dictionary dictionary = new Dictionary();
        BeanUtil.copyProperties(dictionaryOperationDto,dictionary);
        dictionaryDao.update(dictionary,new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getDictType,dictionary.getDictType())
                .eq(Dictionary::getDictName,dictionary.getDictName())
                .eq(Dictionary::getDictValue,dictionary.getDictValue()));
    }

    @Override
    @Transactional
    public void deleteDictionary(DictionaryOperationDto dictionaryOperationDto) {
        Dictionary dictionary = new Dictionary();
        BeanUtil.copyProperties(dictionaryOperationDto,dictionary);
        dictionaryDao.delete(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getDictType,dictionary.getDictType())
                .eq(Dictionary::getDictName,dictionary.getDictName())
                .eq(Dictionary::getDictValue,dictionary.getDictValue()));
    }
}
