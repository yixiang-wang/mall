package com.junior.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.model.dao.CategoryMapper;
import com.junior.mall.model.pojo.Category;
import com.junior.mall.model.request.AddCategoryReq;
import com.junior.mall.model.request.UpdateCategoryReq;
import com.junior.mall.model.vo.CategoryVO;
import com.junior.mall.service.CategoryService;
import com.junior.mall.service.exception.CategoryException;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    //插入目录
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCategory(AddCategoryReq category) throws CategoryException {
        Category category1 = categoryMapper.selectByName(category.getName());
        if (category1 != null) {
            throw new CategoryException(MallExceptionEnum.NAME_EXISTED);
        }
        Category category2 = new Category();
        BeanUtils.copyProperties(category, category2);
        int count = categoryMapper.insertSelective(category2);
        if (count != 1) {
            throw new CategoryException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    //更新目录
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(UpdateCategoryReq category) throws CategoryException {
        Category categoryOld = categoryMapper.selectByPrimaryKey(category.getId());
        if (categoryOld == null) {
            throw new CategoryException(MallExceptionEnum.NOT_FOUND);
        }
        Category categoryNew = new Category();
        BeanUtils.copyProperties(category, categoryNew);
        int count = categoryMapper.updateByPrimaryKeySelective(categoryNew);
        if (count != 1) {
            throw new CategoryException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Integer id) throws CategoryException {
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category == null) {
            throw new CategoryException(MallExceptionEnum.NOT_FOUND);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count != 1) {
            throw new CategoryException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public PageInfo selectAllAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectAll();
        PageInfo pageInfo = new PageInfo<>(categoryList);
        return pageInfo;
    }

    @Override
    @Cacheable(value = "selectAllUser")
    public List<CategoryVO> selectAllUser(Integer parentId) {
        List<CategoryVO> voList = new ArrayList<>();
        getList(voList, parentId);
        return voList;
    }


    private void getList(List<CategoryVO> voList, Integer parentId) {
        List<Category> categoryList = categoryMapper.selectByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (int i = 0; i < categoryList.size(); i++) {
                Category category = categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                getList(categoryVO.getChildCategory(), categoryVO.getId());
                voList.add(categoryVO);
            }
        }


    }


}
