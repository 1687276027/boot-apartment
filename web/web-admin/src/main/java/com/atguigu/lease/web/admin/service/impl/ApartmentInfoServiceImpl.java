package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private  RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentFacilityService facilityService;

    @Autowired
    private ApartmentFeeValueService feeValueService;

    @Autowired
    private ApartmentLabelService labelService;

    @Autowired
    private GraphInfoService graphInfoService;

    /**
     * 保存或更新公寓信息
     *
     * @param apartmentSubmitVo 公寓信息提交对象
     *                          若传入公寓ID为空，则执行插入操作；若不为空，则执行更新操作
     *                          更新操作时会先删除原有配套、杂费、标签和图片信息，再插入新的信息
     */
    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        //保存或修改公寓信息：
        //1.判断该参数id是否为空，为空：插入的新数据 不为空：修改数据
        boolean isUpdate = apartmentSubmitVo.getId() != null;
        //2.调用父类保存或修改方法，将公寓基本信息进行保存
        super.saveOrUpdate(apartmentSubmitVo);

        if (isUpdate) {
            //修改数据：直接将所有原数据删除后重新插入
            //1.删除配套
            LambdaQueryWrapper<ApartmentFacility> facilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            facilityLambdaQueryWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            facilityService.remove(facilityLambdaQueryWrapper);

            //2.删除杂费
            LambdaQueryWrapper<ApartmentFeeValue> feeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            feeValueLambdaQueryWrapper.eq(ApartmentFeeValue::getFeeValueId, apartmentSubmitVo.getId());
            feeValueService.remove(feeValueLambdaQueryWrapper);

            //3.删除标签
            LambdaQueryWrapper<ApartmentLabel> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            labelLambdaQueryWrapper.eq(ApartmentLabel::getLabelId, apartmentSubmitVo.getId());
            labelService.remove(labelLambdaQueryWrapper);

            //4.删除图片
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphQueryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);
        }

        //1.插入配套
        List<Long> facilityInfoIdsList = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIdsList)) {
            ArrayList<ApartmentFacility> facilityArrayList = new ArrayList<>();
            for (Long facilityId : facilityInfoIdsList) {
                ApartmentFacility apartmentFacility = ApartmentFacility.builder().build();
                apartmentFacility.setFacilityId(facilityId);
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                facilityArrayList.add(apartmentFacility);
            }
            facilityService.saveBatch(facilityArrayList);
        }

        //2.插入杂费
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = ApartmentFeeValue.builder().build();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            feeValueService.saveBatch(apartmentFeeValueList);
        }

        //3.插入标签
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            ArrayList<ApartmentLabel> apartmentLabelArrayList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = ApartmentLabel.builder().build();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                apartmentLabelArrayList.add(apartmentLabel);
            }
            labelService.saveBatch(apartmentLabelArrayList);
        }

        //4.插入图片
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }

    }

    /**
     * 分页查询公寓项目列表
     *
     * @param page 分页信息
     * @param queryVo 查询条件
     * @return 公寓项目列表的分页结果
     */
    @Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageItem(page, queryVo);
    }

    /**
     * 根据公寓ID获取公寓详细信息
     *
     * @param id 公寓ID
     * @return ApartmentDetailVo 公寓详细信息对象
     */
    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //1.查询公寓基本信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);

        //2.查询图片列表
        List<GraphVo> graphVoList = graphInfoMapper.selectGraphVoList(id, ItemType.APARTMENT);

        //3.查询标签列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectLabelInfoList(id); //通过公寓id查询出所有标签id，在通过标签id查出所有标签信息

        //4.查询配套列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectFacilityInfoList(id); //通过公寓id查询出所有配套id，在通过配套id查出所有配套信息

        //5.查询杂费列表
        List<FeeValueVo> feeValueList = feeValueMapper.selectFeeValueList(id); //通过公寓id查询出所有杂费值id，在通过杂费值id查出所有杂费以及杂费名称id，再通过杂费名称id查出杂费名

        //6.组装结果
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueList);

        return apartmentDetailVo;
    }

    /**
     * 根据公寓ID删除公寓信息
     *
     * @param id 公寓ID
     * @throws LeaseException 当公寓下存在未删除的房间信息时，抛出该异常
     */
    @Override
    public void removeApartmentById(Long id) {

        //判断公寓底下还有没有未删除的房间信息
        LambdaQueryWrapper<RoomInfo> roomInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoLambdaQueryWrapper.eq(RoomInfo::getApartmentId, id);
        Long count = roomInfoMapper.selectCount(roomInfoLambdaQueryWrapper);

        if (count > 0) {
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);
        }

        super.removeById(id);

        //1.删除图片
        LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphInfoService.remove(graphInfoLambdaQueryWrapper);

        //2.删除标签
        LambdaQueryWrapper<ApartmentLabel> apartmentLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentLabelLambdaQueryWrapper.eq(ApartmentLabel::getApartmentId, id);
        labelService.remove(apartmentLabelLambdaQueryWrapper);

        //3.删除配套
        LambdaQueryWrapper<ApartmentFacility> apartmentFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentFacilityLambdaQueryWrapper.eq(ApartmentFacility::getApartmentId, id);
        facilityService.remove(apartmentFacilityLambdaQueryWrapper);

        //4.删除杂费
        LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentFeeValueLambdaQueryWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        feeValueService.remove(apartmentFeeValueLambdaQueryWrapper);
    }
}




