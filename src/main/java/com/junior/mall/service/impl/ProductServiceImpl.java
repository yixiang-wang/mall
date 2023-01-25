package com.junior.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.model.dao.ProductMapper;
import com.junior.mall.model.pojo.Product;
import com.junior.mall.model.query.ProductListQuery;
import com.junior.mall.model.request.AddProductReq;
import com.junior.mall.model.request.ListProductReq;
import com.junior.mall.model.request.UpdateProductReq;
import com.junior.mall.model.vo.CategoryVO;
import com.junior.mall.service.CategoryService;
import com.junior.mall.service.ProductService;
import com.junior.mall.service.exception.ProductException;
import com.junior.mall.utils.Constant;
import com.junior.mall.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(AddProductReq productReq) throws ProductException {
        Product product1 = productMapper.selectByName(productReq.getName());
        if (product1 != null) {
            throw new ProductException(MallExceptionEnum.NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(productReq, product);
        int count = productMapper.insertSelective(product);
        if (count != 1) {
            throw new ProductException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(UpdateProductReq productReq) throws ProductException {
        Product product1 = productMapper.selectByPrimaryKey(productReq.getId());
        if (product1 == null) {
            throw new ProductException(MallExceptionEnum.NOT_FOUND);
        }
        if (productReq.getName() != null) {
            Product productOld = productMapper.selectByName(productReq.getName());
            if (productOld != null && !productOld.getId().equals(productReq.getId())) {
                throw new ProductException(MallExceptionEnum.NAME_EXISTED);
            }
        }
        Product product = new Product();
        BeanUtils.copyProperties(productReq, product);

        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count != 1) {
            throw new ProductException(MallExceptionEnum.UPDATE_FAILED);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Integer id) throws ProductException {
        Product product1 = productMapper.selectByPrimaryKey(id);
        if (product1 == null) {
            throw new ProductException(MallExceptionEnum.NOT_FOUND);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count != 1) {
            throw new ProductException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.updateStatusBatch(ids, sellStatus);
    }

    @Override
    public PageInfo selectAllAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectAll();
        PageInfo pageInfo = new PageInfo<>(productList);
        return pageInfo;
    }

    @Override
    public PageInfo userList(ListProductReq productReq) {
        ProductListQuery query = new ProductListQuery();
        //判断是否含有关键字
        if (StringUtils.hasLength(productReq.getKeyword())) {
            //关键词前后添加百分号后添加给Query对象
            String keyword = new StringBuilder().append("%").append(productReq.getKeyword()).append("%").toString();
            query.setKeyword(keyword);
        }

        //判断是否指定目录，并查找出目录下的子目录
        if (productReq.getCategoryId() != null) {
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productReq.getCategoryId());
            List<CategoryVO> voList = categoryService.selectAllUser(productReq.getCategoryId());
            getCategoryIds(voList, categoryIds);
            query.setCategoryIds(categoryIds);
        }
        String orderBy = productReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ORDER_ENUM.contains(orderBy)) {
            PageHelper.startPage(productReq.getPageNum(), productReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productReq.getPageNum(), productReq.getPageSize());
        }
        List<Product> productList = productMapper.selectListByUser(query);
        PageInfo pageInfo = new PageInfo<>(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> voList, List<Integer> categoryIds) {
        for (int i = 0; i < voList.size(); i++) {
            CategoryVO vo = voList.get(i);
            if (vo != null) {
                categoryIds.add(vo.getId());
                getCategoryIds(vo.getChildCategory(), categoryIds);
            }
        }
    }

    @Override
    public Product getDetail(Integer id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchByExcel(File destFile) throws IOException, ProductException {
        List<Product> productList=excelToProductList(destFile);
        for (int i=0;i<productList.size();i++){
            Product product=productList.get(i);
            Product oldProduct = productMapper.selectByName(product.getName());
            if (oldProduct != null) {
                continue;
            }
            int count = productMapper.insertSelective(product);
            if (count == 0) {
                throw new ProductException(MallExceptionEnum.INSERT_FAILED);
            }
        }
    }

    private List<Product> excelToProductList(File excel) throws IOException {
        List<Product> productList=new ArrayList<>();
        FileInputStream inputStream=new FileInputStream(excel);
        XSSFWorkbook workbook=new XSSFWorkbook(inputStream);
        XSSFSheet sheet =workbook.getSheetAt(0);
        Iterator<Row> rowIterator=sheet.rowIterator();
        while (rowIterator.hasNext()){
            Row nextRow=rowIterator.next();
            Iterator<Cell> cellIterator=nextRow.cellIterator();
            Product product=new Product();
            while (cellIterator.hasNext()){
                Cell nextCell=cellIterator.next();
                Integer columnIndex=nextCell.getColumnIndex();
                switch (columnIndex){
                    case 0:
                        product.setName((String) ExcelUtils.getCellValue(nextCell));
                        break;
                    case 1:
                        product.setImage((String) ExcelUtils.getCellValue(nextCell));
                        break;
                    case 2:
                        product.setDetail((String) ExcelUtils.getCellValue(nextCell));
                        break;
                    case 3:
                        product.setCategoryId(((Double) ExcelUtils.getCellValue(nextCell)).intValue());
                        break;
                    case 4:
                        product.setStock(((Double) ExcelUtils.getCellValue(nextCell)).intValue());
                        break;
                    case 5:
                        product.setPrice(((Double) ExcelUtils.getCellValue(nextCell)).intValue());
                        break;
                    case 6:
                        product.setStatus(((Double) ExcelUtils.getCellValue(nextCell)).intValue());
                        break;
                    default:
                        break;
                }
            }
            productList.add(product);
        }
        //完成后关闭输入流及工作簿
        workbook.close();
        inputStream.close();
        return productList;
    }
}
